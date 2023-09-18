package com.springframework.spring6restmvc.repositories;

import com.springframework.spring6restmvc.entities.Beer;
import com.springframework.spring6restmvc.model.BeerStyle;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class BeerRepositoryTest {
    @Autowired
    BeerRepository beerRepository;

    @Test
    void testSaveBeerWithTooLongUpc() {
        // DataIntegrityViolationException caused by @Column(length = 10) annotation on the upc property.
        assertThrows(DataIntegrityViolationException.class, () -> {
            Beer savedBeer = beerRepository.save(Beer.builder()
                    .beerName("My beer")
                    .beerStyle(BeerStyle.GOSE)
                    .upc("0123456789101221") // annotated by @Column(length = 10) --> length is checked WHILE writing to DB.
                    .price(new BigDecimal("12.99"))
                    .build());

            // tells Hibernate to immediately write data to the DB.
            beerRepository.flush();
        });

    }

    @Test
    void testSaveBeerWithTooLongName() {
        // ConstraintViolationException caused by @Size annotation on the beerName property
        assertThrows(ConstraintViolationException.class, () -> {
            Beer savedBeer = beerRepository.save(Beer.builder()
                    .beerName("My beer asdasdadsasdasdad asdadsassdadsasd asdasdasd asdadasdasdasdasdas asdasdasd asdasd")
                    .beerStyle(BeerStyle.GOSE)
                    .upc("134563545")// annotated by @Size(max=50) and @Column(length = 10) --> size is checked BEFORE writing to DB.
                    .price(new BigDecimal("12.99"))
                    .build());

            // tells Hibernate to immediately write data to the DB.
            beerRepository.flush();
        });

    }

    @Test
    void testSaveBeer() {
        Beer savedBeer = beerRepository.save(Beer.builder()
                        .beerName("My beer")
                        .beerStyle(BeerStyle.GOSE)
                        .upc("134563545")
                        .price(new BigDecimal("12.99"))
                .build());

        // tells Hibernate to immediately write data to the DB.
        beerRepository.flush();

        assertThat(savedBeer).isNotNull();
        assertThat(savedBeer.getId()).isNotNull();
    }
}
package com.springframework.spring6restmvc.repositories;

import com.springframework.spring6restmvc.entities.Beer;
import com.springframework.spring6restmvc.entities.Category;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class CategoryRepositoryTest {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BeerRepository beerRepository;

    Beer testBeer;

    @BeforeEach
    void setUp() {
        testBeer = beerRepository.findAll().get(0);
    }

    @Transactional
    @Test
    void testAddCategory() {
        Category savedCategory = categoryRepository.save(
                Category.builder()
                        .description("Ales")
                        .build()
        );

        testBeer.addCategory(savedCategory);
        Beer savedBeer = beerRepository.save(testBeer);

        // When putting a break point in the following line we can check the savedBeer and its categories.
        log.debug("saved beer: {}", savedBeer.getBeerName());

        assertThat(savedCategory.getDescription()).isEqualTo("Ales");
        assertThat(savedBeer.getCategories().contains(savedCategory)).isTrue();
        assertThat(savedCategory.getBeers().contains(savedBeer)).isTrue();
    }
}
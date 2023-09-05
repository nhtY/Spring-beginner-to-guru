package com.springframework.spring6restmvc.controller;

import com.springframework.spring6restmvc.model.BeerDTO;
import com.springframework.spring6restmvc.repositories.BeerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// Integration test for BeerController
// Test the controller and its integration with JPA data layer
@SpringBootTest // not a splice of the app, it is a complete test having full application context beans
class BeerControllerIT {

    @Autowired
    BeerController beerController;

    @Autowired
    BeerRepository beerRepository;

    @Test
    void testListBeers() {
        List<BeerDTO> dtos = beerController.listBeers();

        assertThat(dtos.size()).isEqualTo(3); // we know that CommandLineRunner will create and save 3 beers.
    }


    // jpa test do rollback automatically, but we are using controller layer's methods here
    @Rollback // rollback after test passed or failed.
    @Transactional // this is required to be able to roll back
    @Test
    void testEmptyBeerList() {
        // test if controller returns empty list, (not null, just empty list)

        beerRepository.deleteAll(); // this will affect other test. So after the test, a rollback can reset the state
        List<BeerDTO> dtos = beerController.listBeers();

        assertThat(dtos).isNotNull();
        assertThat(dtos.size()).isEqualTo(0);
    }
}
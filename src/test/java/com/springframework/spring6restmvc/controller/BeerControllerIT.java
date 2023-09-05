package com.springframework.spring6restmvc.controller;

import com.springframework.spring6restmvc.model.BeerDTO;
import com.springframework.spring6restmvc.repositories.BeerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
}
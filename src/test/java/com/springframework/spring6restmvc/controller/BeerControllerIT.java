package com.springframework.spring6restmvc.controller;

import com.springframework.spring6restmvc.entities.Beer;
import com.springframework.spring6restmvc.mappers.BeerMapper;
import com.springframework.spring6restmvc.model.BeerDTO;
import com.springframework.spring6restmvc.repositories.BeerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Integration test for BeerController
// Test the controller and its integration with JPA data layer
@SpringBootTest // not a splice of the app, it is a complete test having full application context beans
class BeerControllerIT {

    @Autowired
    BeerController beerController;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    BeerMapper beerMapper;

    @Test
    void testUpdateByIdNotFound() {

        assertThrows(NotFoundException.class, () -> {
            beerController.updateById(UUID.randomUUID(), BeerDTO.builder().build());
        });

    }

    @Test
    void testUpdateById() {
        // If we can update, we already have a BeerDTO. Simulate the resource gathering process:
        Beer beer = beerRepository.findAll().get(0);
        BeerDTO beerDTO = beerMapper.beerToBeerDto(beer);

        // Now, change some properties of the gathered resource
        final String beerName = "UPDATED";
        beerDTO.setBeerName(beerName);

        // when client updates resource, He will not send id(cannot be changed) and version(determined by Hibernate) data:
        // set them null
        beerDTO.setId(null);
        beerDTO.setVersion(null);

        // Now, can update:
        ResponseEntity responseEntity = beerController.updateById(beer.getId(), beerDTO);

        // check if response has the status 204 NO CONTENT
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // really updated?
        Beer updatedBeer = beerRepository.findById(beer.getId()).get();
        assertThat(updatedBeer.getBeerName()).isEqualTo(beerDTO.getBeerName());

    }

    @Rollback
    @Transactional
    @Test
    void testSaveNewBeer() {
        // dto to be posted
        BeerDTO dto = BeerDTO.builder()
                .beerName("New Beer")
                .build();

        // post it and get the returned result
        ResponseEntity responseEntity = beerController.handlePost(dto);

        // Check if status is 201 CREATED and response has 'Location' header.
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();

        // is it really saved?
        // From location header, extract the UUID of the created resource
        String[] locationUUID = responseEntity.getHeaders().getLocation().getPath().split("/");
        // api/v1/beer/{beerId} so the 4th element -> index=3
        UUID savedUUID = UUID.fromString(locationUUID[3]);

        // Use extracted UUID to find recently created resource in DB.
        Beer beer = beerRepository.findById(savedUUID).get();
        assertThat(beer).isNotNull();

    }

    @Test
    void testGetByIdNotFoundError() {
        assertThrows(NotFoundException.class,() -> beerController.getBeerById(UUID.randomUUID()));
    }

    @Test
    void testGetBeerById() {

        Beer testBeer = beerRepository.findAll().get(0);

        BeerDTO dto = beerController.getBeerById(testBeer.getId());

        assertThat(dto).isNotNull();
    }

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
package com.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springframework.spring6restmvc.entities.Beer;
import com.springframework.spring6restmvc.mappers.BeerMapper;
import com.springframework.spring6restmvc.model.BeerDTO;
import com.springframework.spring6restmvc.model.BeerStyle;
import com.springframework.spring6restmvc.repositories.BeerRepository;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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

    // Let's use MockMvc to mock request from client. To do so, we need MockMvc bean in the WebApplicationContext
    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // build a MockMvc instance in the context
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void testListBeerByNameAndStyleQueryParamShowInventoryFalse() throws Exception {

        mockMvc.perform(get(BeerController.BEER_PATH)
                        .queryParam("beerName", "IPA")
                        .queryParam("beerStyle", BeerStyle.IPA.name())
                        .queryParam("showInventory", "False"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(310)))
                .andExpect(jsonPath("$.[0].quantityOnHand").value(IsNull.nullValue()));
    }

    @Test
    void testListBeerByNameAndStyleQueryParamShowInventoryTrue() throws Exception {

        mockMvc.perform(get(BeerController.BEER_PATH)
                        .queryParam("beerStyle", BeerStyle.IPA.name())
                        .queryParam("beerName", "IPA")
                        .queryParam("showInventory", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(310)))
                .andExpect(jsonPath("$.[0].quantityOnHand").value(IsNull.notNullValue()));
    }
    @Test
    void testListBeerByNameAndStyleQueryParam() throws Exception {

        mockMvc.perform(get(BeerController.BEER_PATH)
                        .queryParam("beerStyle", BeerStyle.IPA.name())
                        .queryParam("beerName", "IPA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(310)));
    }

    @Test
    void testListBeerByStyleQueryParam() throws Exception {

        mockMvc.perform(get(BeerController.BEER_PATH)
                        .queryParam("beerStyle", BeerStyle.IPA.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(548)));
    }

    @Test
    void testListBeerByNameQueryParam() throws Exception {

        mockMvc.perform(get(BeerController.BEER_PATH)
                .queryParam("beerName", "IPA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(336)));
    }

    @Test
    void testPatchByIdNotFound() {
        assertThrows(NotFoundException.class, () -> {
            beerController.patchBeerById(UUID.randomUUID(), BeerDTO.builder().build());
        });
    }

    @Test
    void patchBeerByIdTooLongName() throws Exception {
        Beer testBeer = beerRepository.findAll().get(0);

        // following Map represents the patch - properties wanted to be updated
        Map<String, Object> patchMap = new HashMap<>();
        patchMap.put("beerName", "Very Long Beer Name asdasd asdsadasd asdadsasd asdasdasd asdasdasd asdasdasd");


        // HTTP PATCH .../api/v1/beer/{beerId}
        // add 'Accept' header to tell json results accepted
        // add 'Content-Type' header to tell client sending a json
        // write patch into the body of the request
        // Then, check if response has status 400 BAD REQUEST
        MvcResult result = mockMvc.perform(patch(BeerController.BEER_PATH_ID, testBeer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchMap)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)))
                .andReturn();

        System.out.println("Response content for bad beerName: " + result.getResponse().getContentAsString());

    }

    @Rollback
    @Transactional
    @Test
    void testPatchBeerById() {
        Beer beer = beerRepository.findAll().get(0);
        BeerDTO dto = beerMapper.beerToBeerDto(beer);

        dto.setId(null);
        dto.setVersion(null);

        final String updatedName = "Updated";
        final Double newPrice = 8.99;
        dto.setBeerName(updatedName);
        dto.setPrice(BigDecimal.valueOf(newPrice));

        ResponseEntity responseEntity = beerController.patchBeerById(beer.getId(), dto);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Beer saved = beerRepository.findById(beer.getId()).get();

        assertThat(saved.getBeerName()).isEqualTo(updatedName);
        assertThat(saved.getPrice()).isEqualTo(BigDecimal.valueOf(newPrice));

    }

    @Test
    void testDeleteByIdNotFound() {
        assertThrows(NotFoundException.class, () -> {
            beerController.deleteById(UUID.randomUUID());
        });
    }

    @Rollback
    @Transactional
    @Test
    void deleteById() {
        Beer beer = beerRepository.findAll().get(0);

        ResponseEntity responseEntity = beerController.deleteById(beer.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // really deleted?
        assertThat(beerRepository.findById(beer.getId())).isEmpty();
    }

    @Test
    void testUpdateByIdNotFound() {

        assertThrows(NotFoundException.class, () -> {
            beerController.updateById(UUID.randomUUID(), BeerDTO.builder().build());
        });

    }

    @Rollback
    @Transactional
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
        List<BeerDTO> dtos = beerController.listBeers(null, null, null);

        assertThat(dtos.size()).isEqualTo(2413); // we know that CommandLineRunner will create and save 3 beers.
    }


    // jpa test do rollback automatically, but we are using controller layer's methods here
    @Rollback // rollback after test passed or failed.
    @Transactional // this is required to be able to roll back
    @Test
    void testEmptyBeerList() {
        // test if controller returns empty list, (not null, just empty list)

        beerRepository.deleteAll(); // this will affect other test. So after the test, a rollback can reset the state
        List<BeerDTO> dtos = beerController.listBeers(null, null, null);

        assertThat(dtos).isNotNull();
        assertThat(dtos.size()).isEqualTo(0);
    }
}
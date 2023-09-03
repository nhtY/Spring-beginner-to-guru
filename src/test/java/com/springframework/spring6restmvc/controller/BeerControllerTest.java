package com.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springframework.spring6restmvc.model.Beer;
import com.springframework.spring6restmvc.services.BeerService;
import com.springframework.spring6restmvc.services.BeerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


//@SpringBootTest we are not testing full application (we do not need all the beans, application context, etc.)
@WebMvcTest(BeerController.class) // we are just testing BeerController (if not specified all controllers are generated)
class BeerControllerTest {

//    @Autowired
//    BeerController beerController;
    @Autowired
    MockMvc mockMvc; // will be used to create mock/fake requests and responses instead of via a running server

    @Autowired
    ObjectMapper objectMapper;

    // Our controller needs BeerService object. So, create a fake instance of that class to be able to test the controller properly:
    @MockBean
    BeerService beerService;

    // to reach the beer objects in the hashmap of BeerServiceImpl
    BeerServiceImpl beerServiceImpl;

    @BeforeEach // before each test method, run the following
    void setUp() {
        beerServiceImpl = new BeerServiceImpl(); // each test method will have separate beerServiceImpl
    }

    @Test
    void testCreateNewBeer() throws Exception {
        // following beer represents the beer sent by client. So, its id and version data are null
        Beer testBeer = beerServiceImpl.listBeers().get(0);
        testBeer.setId(null);
        testBeer.setVersion(null);

        // when handler method receives any beer object it will return the second beer item from our list.
        // That beer object represents the created beer. So, it will have id and version data.
        given(beerService.saveNewBeer(any(Beer.class))).willReturn(beerServiceImpl.listBeers().get(1));

        // HTTP POST .../api/v1/beer
        // set accept header to application/json
        // in body of the request write the beer object in json format
        // Then, check if response code is 201 Content Created
        // Then, check if response header contains 'Location' header.
        mockMvc.perform(post("/api/v1/beer")
                .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testBeer)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void testListBeers() throws Exception {
        // When mock beenService's listBeer method is called, return the result of beerServiceImpl.listBeers()
        given(beerService.listBeers()).willReturn(beerServiceImpl.listBeers());

        // HTTP GET .../api/v1/beer
        // add 'Accept' header with value application json
        // Then, check if response code is 200 OK
        // Then, check if content type is json
        // Then, check if the returned list's length is 3
        mockMvc.perform(get("/api/v1/beer")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(3)));
    }

    @Test
    void getBeerById() throws Exception {

        // get the first beer in the list to use as a test object
        Beer testBeer = beerServiceImpl.listBeers().get(0);

        // if method returns a beer, then mockito will return the testBeer.
        given(beerService.getBeerById(testBeer.getId())).willReturn(testBeer);

        // HTTP GET : .../api/v1/beer/{UUID}
        // write 'Accept' header into the request.
        // Then, expect that the controller returns with HTTP 200 OK.
        // Then, check if there is a result as content and if its type is JSON
        // Then, check if returned beer id = test beer id
        // Then, check if returned beer name = test beer name
        mockMvc.perform(get("/api/v1/beer/" + testBeer.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // for jsonPath usage: https://github.com/json-path/JsonPath
                .andExpect(jsonPath("$.id", is(testBeer.getId().toString())))
                .andExpect(jsonPath("$.beerName", is(testBeer.getBeerName())));
    }
}
package com.springframework.spring6restmvc.controller;

import com.springframework.spring6restmvc.model.Beer;
import com.springframework.spring6restmvc.services.BeerService;
import com.springframework.spring6restmvc.services.BeerServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

//@SpringBootTest we are not testing full application (we do not need all the beans, application context, etc.)
@WebMvcTest(BeerController.class) // we are just testing BeerController (if not specified all controllers are generated)
class BeerControllerTest {

//    @Autowired
//    BeerController beerController;
    @Autowired
    MockMvc mockMvc;

    // Our controller needs BeerService object. So, create a fake instance of that class to be able to test the controller properly:
    @MockBean
    BeerService beerService;

    // to reach the beer objects in the hashmap of BeerServiceImpl
    BeerServiceImpl beerServiceImpl = new BeerServiceImpl();

    @Test
    void getBeerById() throws Exception {

        // get the first beer in the list to use as a test object
        Beer testBeer = beerServiceImpl.listBeers().get(0);

        // for any UUID if method returns a beer, then mockito will return the testBeer.
        given(beerService.getBeerById(any(UUID.class))).willReturn(testBeer);

        // HTTP GET : .../api/v1/beer/{UUID}
        // write 'Accept' header into the request.
        // Then, expect that the controller returns with HTTP 200 OK.
        // Then, check if there is a result as content and if its type is JSON
        mockMvc.perform(get("/api/v1/beer/" + UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
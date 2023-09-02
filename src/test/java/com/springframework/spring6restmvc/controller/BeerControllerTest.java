package com.springframework.spring6restmvc.controller;

import com.springframework.spring6restmvc.services.BeerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    void getBeerById() throws Exception {
//        System.out.println(beerController.getBeerById(UUID.randomUUID()));

        // HTTP GET : .../api/v1/beer/{UUID}
        // write 'Accept' header into the request.
        // Then, expect that the controller returns with HTTP 200 OK.
        mockMvc.perform(get("/api/v1/beer/" + UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
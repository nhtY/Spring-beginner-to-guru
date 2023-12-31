package com.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springframework.spring6restmvc.model.BeerDTO;
import com.springframework.spring6restmvc.services.BeerService;
import com.springframework.spring6restmvc.services.BeerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    ArgumentCaptor<BeerDTO> beerArgumentCaptor;

    // to reach the beer objects in the hashmap of BeerServiceImpl
    BeerServiceImpl beerServiceImpl;

    @BeforeEach // before each test method, run the following
    void setUp() {
        beerServiceImpl = new BeerServiceImpl(); // each test method will have separate beerServiceImpl
    }

    @Test
    void testCreateBeerWithNullBeerName() throws Exception {

        BeerDTO testBeerDTO = BeerDTO.builder().build(); // properties are all null

        given(beerService.saveNewBeer(any(BeerDTO.class))).willReturn(beerServiceImpl.listBeers(null, null, null, 1, 25).getContent().get(1));

        // Test if @Validated annotation throws error when null beerName is received.
        // Now, the response body is: [{"beerName":"must not be blank"},{"beerName":"must not be null"}]
        MvcResult result =  mockMvc.perform(post(BeerController.BEER_PATH)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testBeerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(6))) // expecting 6 errors: NotNull or NotBlank annotations used for several properties
        .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void patchBeerById() throws Exception {
        BeerDTO testBeer = beerServiceImpl.listBeers(null, null, null, 1, 25).getContent().get(0);

        // following Map represents the patch - properties wanted to be updated
        Map<String, Object> patchMap = new HashMap<>();
        patchMap.put("beerName", "New Beer Name");

        BeerDTO dto = BeerDTO.builder()
                .id(testBeer.getId())
                .beerName(patchMap.get("beerName").toString())
                .upc(testBeer.getUpc())
                .version(testBeer.getVersion())
                .price(testBeer.getPrice())
                .quantityOnHand(testBeer.getQuantityOnHand())
                .build();

        given(beerService.patchBeerById(any(UUID.class), any(BeerDTO.class))).willReturn(Optional.of(dto));

        // HTTP PATCH .../api/v1/beer/{beerId}
        // add 'Accept' header to tell json results accepted
        // add 'Content-Type' header to tell client sending a json
        // write patch into the body of the request
        // Then, check if response has status 204 NO CONTENT
        mockMvc.perform(patch(BeerController.BEER_PATH_ID, testBeer.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(patchMap)))
                .andExpect(status().isNoContent());

        // Verify that our mock beerService's patchBeerById() method is called.
        // And capture the arguments passed to the method.
        verify(beerService).patchBeerById(uuidArgumentCaptor.capture(), beerArgumentCaptor.capture());

        // Make sure that passed UUID and Object are what we actually passed.
        assertThat(testBeer.getId().equals(uuidArgumentCaptor.getValue()));
        assertThat(patchMap.get("beerName").equals(beerArgumentCaptor.getValue().getBeerName()));
    }


    @Test
    void deleteById() throws Exception {
        BeerDTO testBeer = beerServiceImpl.listBeers(null, null, null, 1, 25).getContent().get(0);

        // service's deleteById() returns a Boolean. Fake it:
        given(beerService.deleteById(any(UUID.class))).willReturn(true);

        mockMvc.perform(delete(BeerController.BEER_PATH_ID, testBeer.getId().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(beerService).deleteById(uuidArgumentCaptor.capture());

        // Make sure that passed argument is the same as we passed when performing HTTP PUT above.
        assertThat(testBeer.getId().equals(uuidArgumentCaptor.getValue()));
    }

    @Test
    void testUpdateByIdBlankName() throws Exception {
        BeerDTO testBeer = beerServiceImpl.listBeers(null, null, null, 1, 25).getContent().get(0);

        testBeer.setBeerName(""); // set name blank

        // When beer controller invokes updateById() of the service, service returns the updated resource. Imitate it
        given(beerService.updateById(any(UUID.class), any(BeerDTO.class))).willReturn(Optional.of(testBeer));

        // HTTP PUT .../api/v1/beer/{beerId}
        // add 'Accept' header with value application/json
        // add 'Content-Type' header with application/json to indicate the format of the content which is being sent
        // write the beer object in json format into the body of the request
        // Then, check if the response has status code 400 BAD REQUEST
        mockMvc.perform(put(BeerController.BEER_PATH_ID, testBeer.getId().toString())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBeer)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1))); // [NotBlank]
    }

    @Test
    void updateById() throws Exception {
        BeerDTO testBeer = beerServiceImpl.listBeers(null, null, null, 1, 25).getContent().get(0);

        // When beer controller invokes updateById() of the service, service returns the updated resource. Imitate it
        given(beerService.updateById(any(UUID.class), any(BeerDTO.class))).willReturn(Optional.of(testBeer));

        // HTTP PUT .../api/v1/beer/{beerId}
        // add 'Accept' header with value application/json
        // add 'Content-Type' header with application/json to indicate the format of the content which is being sent
        // write the beer object in json format into the body of the request
        // Then, check if the response has status code 204 NO CONTENT
        mockMvc.perform(put(BeerController.BEER_PATH_ID, testBeer.getId().toString())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testBeer)))
                .andExpect(status().isNoContent());

        // we need to verify that handler method called our mock beerService's updateById() method.
        // Verify that updateById() is called with any UUID and Beer parameters:
        verify(beerService).updateById(any(UUID.class), any(BeerDTO.class));
    }

    @Test
    void testCreateNewBeer() throws Exception {
        // following beer represents the beer sent by client. So, its id and version data are null
        BeerDTO testBeer = beerServiceImpl.listBeers(null, null, null, 1, 25).getContent().get(0);
        testBeer.setId(null);
        testBeer.setVersion(null);

        // when handler method receives any beer object it will return the second beer item from our list.
        // That beer object represents the created beer. So, it will have id and version data.
        given(beerService.saveNewBeer(any(BeerDTO.class))).willReturn(beerServiceImpl.listBeers(null, null, null, 1, 25).getContent().get(1));

        // HTTP POST .../api/v1/beer
        // set accept header to application/json
        // in body of the request write the beer object in json format
        // Then, check if response code is 201 Content Created
        // Then, check if response header contains 'Location' header.
        mockMvc.perform(post(BeerController.BEER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testBeer)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void testListBeers() throws Exception {
        // When mock beenService's listBeer method is called, return the result of beerServiceImpl.listBeers()
        given(beerService.listBeers(any(), any(), any(), any(), any())).willReturn(beerServiceImpl.listBeers(null, null, null, 1, 25));

        // HTTP GET .../api/v1/beer
        // add 'Accept' header with value application json
        // Then, check if response code is 200 OK
        // Then, check if content type is json
        // Then, check if the returned list's length is 3
        mockMvc.perform(get(BeerController.BEER_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.size()", is(3)));
    }

    @Test
    void getBeerByIdNotFound() throws Exception {
        // When our mock beerService's getBeerById method is called with any UUID parameter
        // it will return empty optional that triggers NotFoundException
        given(beerService.getBeerById(any(UUID.class))).willReturn(Optional.empty());

        // HTTP GET .../api/v1/beer/{beerId}
        // When the controller's handler method interacts with the beerService
        // a NotFoundException will be thrown by controller that may cause test to fail.
        // Check if a response with status 404 NOT FOUND is returned
        mockMvc.perform(get(BeerController.BEER_PATH_ID, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBeerById() throws Exception {

        // get the first beer in the list to use as a test object
        BeerDTO testBeer = beerServiceImpl.listBeers(null, null, null, 1, 25).getContent().get(0);

        // if method returns a beer, then mockito will return the testBeer.
        given(beerService.getBeerById(testBeer.getId())).willReturn(Optional.of(testBeer));

        // HTTP GET : .../api/v1/beer/{UUID}
        // write 'Accept' header into the request.
        // Then, expect that the controller returns with HTTP 200 OK.
        // Then, check if there is a result as content and if its type is JSON
        // Then, check if returned beer id = test beer id
        // Then, check if returned beer name = test beer name
        mockMvc.perform(get(BeerController.BEER_PATH_ID, testBeer.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // for jsonPath usage: https://github.com/json-path/JsonPath
                .andExpect(jsonPath("$.id", is(testBeer.getId().toString())))
                .andExpect(jsonPath("$.beerName", is(testBeer.getBeerName())));
    }
}
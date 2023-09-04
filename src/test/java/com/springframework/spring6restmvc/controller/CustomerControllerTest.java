package com.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springframework.spring6restmvc.model.CustomerDTO;
import com.springframework.spring6restmvc.services.CustomerService;
import com.springframework.spring6restmvc.services.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.core.Is.is;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc; // will be used to create mock/fake requests and responses instead of via a running server

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CustomerService customerService; // CustomerController needs it. So, fake it.

    @Captor // Define a class level argument captor for UUID to reuse it whenever needed.
    ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    ArgumentCaptor<CustomerDTO> customerArgumentCaptor;

    CustomerServiceImpl customerServiceImpl; // to generate test objects for verification

    @BeforeEach // before running each test methods, run the following method
    void setUp() {
        customerServiceImpl = new CustomerServiceImpl();
        // In some test methods we make modifications in customerServiceImpl (setting customer id null etc.).
        // Those changes will affect other tests if they use the modified objects.
        // That is why, we provide a new customerServiceImpl object for each test method separately.
    }


    @Test
    void patchCustomerById() throws Exception {
        CustomerDTO testCustomer = customerServiceImpl.listCustomers().get(0);

        // following map represents the patch - properties to be updated
        Map<String, Object> patchMap = new HashMap<>();
        patchMap.put("name", "New Customer Name");

        // HTTP PATCH .../api/v1/customer/{customerId}
        // add 'Accept' header to tell client accepts json results
        // add 'Content-Type' header to tell client is sending content in json format
        // write patch object in json format into the body
        // Then, check if response has status code 204 NO CONTENT
        mockMvc.perform(patch(CustomerController.CUSTOMER_PATH_ID, testCustomer.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(patchMap)))
                .andExpect(status().isNoContent());

        // verify that mock customerService's patchCustomerById method is called and capture the arguments passed into this method
        verify(customerService).patchCustomerById(uuidArgumentCaptor.capture(), customerArgumentCaptor.capture());

        // Check if the passed arguments are proper.
        assertThat(testCustomer.getId().equals(uuidArgumentCaptor.getValue()));
        assertThat(patchMap.get("name").equals(customerArgumentCaptor.getValue()));

    }

    @Test
    void deleteById() throws Exception {
        CustomerDTO testCustomer = customerServiceImpl.listCustomers().get(0);

        // HTTP DELETE .../api/v1/customer/{customerId}
        // add 'Accept' header
        // Then, check if response has status code 204 NO CONTENT
        mockMvc.perform(delete(CustomerController.CUSTOMER_PATH_ID, testCustomer.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify that mock customerService's deleteById() method is called:
        verify(customerService).deleteById(uuidArgumentCaptor.capture());

        // make sure that deleteById() of mock service is called with proper arguments:
        assertThat(testCustomer.getId().equals(uuidArgumentCaptor.getValue()));

    }


    @Test
    void updateById() throws Exception {
        CustomerDTO testCustomer = customerServiceImpl.listCustomers().get(0);

        // We did not use given() because our handler method or service do not create anything.
        // So, there is no need to create mock/fake objects.

        // HTTP PUT ../api/v1/customer/{customerId}
        // add 'Accept' header telling that client accepts json results
        // add 'Content-Type' header to indicate that client sending json as content in the body
        // write the customer object in JSON format into the content body
        // Then, check if response has status code 204 NO CONTENT
        mockMvc.perform(put(CustomerController.CUSTOMER_PATH_ID, testCustomer.getId().toString())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testCustomer)))
                .andExpect(status().isNoContent());

        // We expect handler method in the CustomerController will call customerService's updateById() method.
        // That is why we provided a mock customerService. So, here check if related method is called.
        // verify updateById() method is called with any UUID and customer parameters:
        verify(customerService).updateById(uuidArgumentCaptor.capture(), any(CustomerDTO.class));

        assertThat(testCustomer.getId().equals(uuidArgumentCaptor.getValue()));
    }

    @Test
    void createNewCustomer() throws Exception {
        // testCustomer represents the customer object sent by client. It has no id nor version.
        // We know that id and version data generated by backend, not on frontend.
        CustomerDTO testCustomer = customerServiceImpl.listCustomers().get(0);
        testCustomer.setId(null);
        testCustomer.setVersion(null);

        // when any customer object received by handler method of the controller, return the second customer item from our list
        // that customer represents the created customer with id and version data
        given(customerService.createNewCustomer(any(CustomerDTO.class))).willReturn(customerServiceImpl.listCustomers().get(1));

        // HTTP POST .../api/v1/customer
        // set 'Accept' header application/json
        // set 'Content-Type' header application/json (I am sending a content in JSON format)
        // write customer data on body
        // Then, check if response has status code 201 Content Created
        // Then, check if response contains 'Location' header.
        mockMvc.perform(post(CustomerController.CUSTOMER_PATH)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCustomer)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

    }

    @Test
    void listCustomers() throws Exception {
        given(customerService.listCustomers()).willReturn(customerServiceImpl.listCustomers());

        mockMvc.perform(get(CustomerController.CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(3)));
    }

    @Test
    void getCustomerByIdNotFound() throws Exception {
        // When mock customerService's getCustomerById() method is called return an empty optional
        given(customerService.getCustomerById(any(UUID.class))).willReturn(Optional.empty());

        // HTTP GET ../api/v1/customer/{customerId}
        // A NotFoundException will be thrown by customer controller that may cause test to fail.
        // Then, check if response has status 404 NOT FOUND.
        mockMvc.perform(get(CustomerController.CUSTOMER_PATH_ID, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCustomerById() throws Exception {

        CustomerDTO testCustomer = customerServiceImpl.listCustomers().get(0);

        given(customerService.getCustomerById(testCustomer.getId())).willReturn(Optional.of(testCustomer));

        mockMvc.perform(get(CustomerController.CUSTOMER_PATH_ID, testCustomer.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testCustomer.getId().toString())))
                .andExpect(jsonPath("$.name", is(testCustomer.getName())));
    }
}
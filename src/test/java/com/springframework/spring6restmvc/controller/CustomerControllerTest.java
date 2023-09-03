package com.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springframework.spring6restmvc.model.Customer;
import com.springframework.spring6restmvc.services.CustomerService;
import com.springframework.spring6restmvc.services.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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

    CustomerServiceImpl customerServiceImpl; // to generate test objects for verification

    @BeforeEach // before running each test methods, run the following method
    void setUp() {
        customerServiceImpl = new CustomerServiceImpl();
        // In some test methods we make modifications in customerServiceImpl (setting customer id null etc.).
        // Those changes will affect other tests if they use the modified objects.
        // That is why, we provide a new customerServiceImpl object for each test method separately.
    }

    @Test
    void createNewCustomer() throws Exception {
        // testCustomer represents the customer object sent by client. It has no id nor version.
        // We know that id and version data generated by backend, not on frontend.
        Customer testCustomer = customerServiceImpl.listCustomers().get(0);
        testCustomer.setId(null);
        testCustomer.setVersion(null);

        // when any customer object received by handler method of the controller, return the second customer item from our list
        // that customer represents the created customer with id and version data
        given(customerService.createNewCustomer(any(Customer.class))).willReturn(customerServiceImpl.listCustomers().get(1));

        // HTTP POST .../api/v1/customer
        // set 'Accept' header application/json
        // set 'Content-Type' header application/json (I am sending a content in JSON format)
        // write customer data on body
        // Then, check if response has status code 201 Content Created
        // Then, check if response contains 'Location' header.
        mockMvc.perform(post("/api/v1/customer")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCustomer)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

    }

    @Test
    void listCustomers() throws Exception {
        given(customerService.listCustomers()).willReturn(customerServiceImpl.listCustomers());

        mockMvc.perform(get("/api/v1/customer")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(3)));
    }

    @Test
    void getCustomerById() throws Exception {

        Customer testCustomer = customerServiceImpl.listCustomers().get(0);

        given(customerService.getCustomerById(testCustomer.getId())).willReturn(testCustomer);

        mockMvc.perform(get("/api/v1/customer/" + testCustomer.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testCustomer.getId().toString())))
                .andExpect(jsonPath("$.name", is(testCustomer.getName())));
    }
}
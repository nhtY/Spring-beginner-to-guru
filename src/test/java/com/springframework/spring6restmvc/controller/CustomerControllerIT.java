package com.springframework.spring6restmvc.controller;


import com.springframework.spring6restmvc.entities.Customer;
import com.springframework.spring6restmvc.model.CustomerDTO;
import com.springframework.spring6restmvc.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CustomerControllerIT {

    @Autowired
    CustomerController customerController;

    @Autowired
    CustomerRepository customerRepository;

    @Test
    void testGetCustomerByIdNotFound() {
        assertThrows(NotFoundException.class, () -> customerController.getCustomerById(UUID.randomUUID()));
    }

    @Test
    void testGetCustomerById() {
        Customer testCustomer = customerRepository.findAll().get(0);

        CustomerDTO dto = customerController.getCustomerById(testCustomer.getId());

        assertThat(dto).isNotNull();
    }

    @Test
    void testListCustomers() {

        List<CustomerDTO> dtos = customerController.listCustomers();

        assertThat(dtos).isNotNull();
        assertThat(dtos.size()).isEqualTo(3);
    }

    @Rollback
    @Transactional
    @Test
    void testEmptyListCustomers() {
        customerRepository.deleteAll();
        List<CustomerDTO> dtos = customerController.listCustomers();

        assertThat(dtos).isNotNull();
        assertThat(dtos.size()).isEqualTo(0);
    }
}
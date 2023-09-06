package com.springframework.spring6restmvc.controller;


import com.springframework.spring6restmvc.entities.Customer;
import com.springframework.spring6restmvc.mappers.CustomerMapper;
import com.springframework.spring6restmvc.model.CustomerDTO;
import com.springframework.spring6restmvc.repositories.CustomerRepository;
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

@SpringBootTest
class CustomerControllerIT {

    @Autowired
    CustomerController customerController;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomerMapper customerMapper;

    @Test
    void testPatchCustomerByIdNotFound() {
        assertThrows(NotFoundException.class, () -> {
           customerController.patchCustomerById(UUID.randomUUID(), CustomerDTO.builder().build());
        });
    }
    @Rollback
    @Transactional
    @Test
    void testPatchCustomerById() {
        Customer customer = customerRepository.findAll().get(0);
        CustomerDTO dto = customerMapper.customerToCustomerDto(customer);

        dto.setId(null);
        dto.setVersion(null);
        final String updatedName = "Updated";
        dto.setName(updatedName);

        ResponseEntity responseEntity = customerController.patchCustomerById(customer.getId(), dto);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(customerRepository.findById(customer.getId()).get().getName()).isEqualTo(updatedName);
    }

    @Test
    void testDeleteByIdNotFound() {

        assertThrows(NotFoundException.class, () -> {
           customerController.deleteById(UUID.randomUUID());
        });

    }

    @Rollback
    @Transactional
    @Test
    void testDeleteById() {
        Customer customer = customerRepository.findAll().get(0);

        ResponseEntity responseEntity = customerController.deleteById(customer.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(customerRepository.findById(customer.getId())).isEmpty();
    }

    @Test
    void testUpdateByIdNotFound() {
        assertThrows(NotFoundException.class, () -> {
            customerController.updateById(UUID.randomUUID(), CustomerDTO.builder().build());
        });
    }

    @Rollback
    @Transactional
    @Test
    void testUpdateById() {
        Customer testCustomer = customerRepository.findAll().get(0);
        CustomerDTO dto = customerMapper.customerToCustomerDto(testCustomer);

        dto.setId(null);
        dto.setVersion(null);
        final String nameUpdated = "UPDATED";
        dto.setName(nameUpdated);

        ResponseEntity responseEntity = customerController.updateById(testCustomer.getId(), dto);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // updated??
        assertThat(customerRepository.findById(testCustomer.getId()).get().getName()).isEqualTo(nameUpdated);

    }

    @Rollback
    @Transactional
    @Test
    void testCreateNewCustomer() {
        CustomerDTO dto = CustomerDTO.builder()
                .name("New Customer")
                .build();

        ResponseEntity responseEntity = customerController.createNewCustomer(dto);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();

        // really created?
        String[] locationUUID = responseEntity.getHeaders().getLocation().getPath().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[3]);

        Customer savedCustomer = customerRepository.findById(savedUUID).get();
        assertThat(savedCustomer).isNotNull();
    }

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
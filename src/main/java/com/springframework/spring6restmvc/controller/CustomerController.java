package com.springframework.spring6restmvc.controller;

import com.springframework.spring6restmvc.model.Customer;
import com.springframework.spring6restmvc.services.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/customer")
public class CustomerController {

    private final CustomerService customerService;

    @PatchMapping("{customerId}")
    public ResponseEntity patchCustomerById(@PathVariable("customerId") UUID customerId, Customer customer) {

        customerService.patchCustomerById(customerId, customer);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("{customerId}")
    public ResponseEntity deleteById(@PathVariable("customerId") UUID customerId){

        customerService.deleteById(customerId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PutMapping("{customerId}")
    public ResponseEntity updateById(@PathVariable("customerId") UUID customerId, @RequestBody Customer customer) {

        customerService.updateById(customerId, customer);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PostMapping
    public ResponseEntity createNewCustomer(@RequestBody Customer customer) {

        Customer savedCustomer = customerService.createNewCustomer(customer);

        log.debug("Customer is saved: {}", savedCustomer);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "api/v1/customer/" + savedCustomer.getId().toString());

        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Customer> listCustomers() {

        log.debug("List All Customers- in CustomerController");

        return customerService.listCustomers();
    }

    @RequestMapping(value = "{customerId}", method = RequestMethod.GET)
    public Customer getCustomerById(@PathVariable("customerId") UUID id) {

        log.debug("Get Customers by Id: {}- in CustomerController", id);

        return customerService.getCustomerById(id);
    }

}

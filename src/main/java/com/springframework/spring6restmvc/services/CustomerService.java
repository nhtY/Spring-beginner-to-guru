package com.springframework.spring6restmvc.services;

import com.springframework.spring6restmvc.model.Customer;

import java.util.List;
import java.util.UUID;

public interface CustomerService {

    List<Customer> listCustomers();

    Customer getCustomerById(UUID id);

    Customer createNewCustomer(Customer customer);
}
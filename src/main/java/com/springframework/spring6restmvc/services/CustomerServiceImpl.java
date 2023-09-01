package com.springframework.spring6restmvc.services;

import com.springframework.spring6restmvc.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    private final Map<UUID, Customer> customerMap;

    public CustomerServiceImpl() {
        customerMap = new HashMap<>();

        Customer customer1 = Customer.builder()
                .id(UUID.randomUUID())
                .name("John Spring")
                .version(1)
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        Customer customer2 = Customer.builder()
                .id(UUID.randomUUID())
                .name("Eva Blue")
                .version(1)
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        Customer customer3 = Customer.builder()
                .id(UUID.randomUUID())
                .name("Joseph Carter")
                .version(1)
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        customerMap.put(customer1.getId(), customer1);
        customerMap.put(customer2.getId(), customer2);
        customerMap.put(customer3.getId(), customer3);
    }

    @Override
    public List<Customer> listCustomers() {

        log.debug("List All Customers- in CustomerService");

        return new ArrayList<>(customerMap.values());
    }

    @Override
    public Customer getCustomerById(UUID id) {

        log.debug("Get Customer by Id: {} - in CustomerService", id);

        return customerMap.get(id);
    }

    @Override
    public Customer createNewCustomer(Customer customer) {

        Customer savedCustomer = Customer.builder()
                .id(UUID.randomUUID())
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .version(1)
                .name(customer.getName())
                .build();

        customerMap.put(savedCustomer.getId(), savedCustomer);

        return savedCustomer;
    }

    @Override
    public void updateById(UUID customerId, Customer customer) {
        Customer existing = customerMap.get(customerId);

        log.debug("Customer before update: {}", existing);

        existing.setName(customer.getName());
        existing.setLastModifiedDate(LocalDateTime.now());

        log.debug("Customer after update: {}", existing);
    }

    @Override
    public void deleteById(UUID customerId) {
        log.debug("Deleting customer with id: {}", customerId);

        customerMap.remove(customerId);
    }

    @Override
    public void patchCustomerById(UUID customerId, Customer customer) {
        Customer existing = customerMap.get(customerId);

        log.debug("Customer before patch: {}", existing);

        if (StringUtils.hasText(customer.getName())) {
            existing.setName(customer.getName());
        }

        log.debug("Customer after patch: {}", existing);
    }
}
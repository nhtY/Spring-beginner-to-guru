package com.springframework.spring6restmvc.repositories;

import com.springframework.spring6restmvc.entities.Beer;
import com.springframework.spring6restmvc.entities.BeerOrder;
import com.springframework.spring6restmvc.entities.Customer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
class BeerOrderRepositoryTest {

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerRepository beerRepository;

    // following customer and beer will be used when generating beer order
    Customer testCustomer;
    Beer testBeer;

    @BeforeEach
    void setUp() {
        testCustomer = customerRepository.findAll().get(0);
        testBeer = beerRepository.findAll().get(0);
    }

    // about lazy initialization: https://www.linkedin.com/pulse/dissecting-hibernate-lazyinitializationexception-cause-jonas-bockstal/
    @Transactional // used due to hibernate lazy initialization exception
    @Test
    void testBeerOrders() {
        BeerOrder beerOrder = BeerOrder.builder()
              .customerRef("Test order")
              .customer(testCustomer)
              .build();

      // BeerOrder savedBeerOrder = beerOrderRepository.save(beerOrder);

        // saveAndFlush method saves it immediately. In this way, a bidirectional relation can be established.
        // Otherwise, order has customer but customer cannot see this order in its orders list.
        BeerOrder savedBeerOrder = beerOrderRepository.saveAndFlush(beerOrder);

        log.debug("saved order: {}", savedBeerOrder.getCustomerRef());

    }
}
package com.springframework.spring6restmvc.bootstrap;

import com.springframework.spring6restmvc.entities.Customer;
import com.springframework.spring6restmvc.repositories.BeerRepository;
import com.springframework.spring6restmvc.repositories.CustomerRepository;
import com.springframework.spring6restmvc.services.BeerCsvService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DataJpaTest
class BootstrapDataTest {

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerCsvService beerCsvService;

    BootstrapData bootstrapData;

    @BeforeEach
    void setUp() {
      bootstrapData = new BootstrapData(beerRepository, customerRepository, beerCsvService);
    }

    @Test
    void run() throws Exception {
        // call the CommandLineRunner's run() method to initial data loading
        bootstrapData.run(null);

        // check if initial data loaded, we have 3 records per tables:
        assertThat(beerRepository.count()).isEqualTo(3);
        assertThat(customerRepository.count()).isEqualTo(3);
    }

    @Test
    void testManualSetIdNotEqualsHibernateGeneratedValue() throws Exception {
        bootstrapData.run(null);

        Customer savedCustomer = customerRepository.findAll().stream()
                .filter(c -> c.getName().equals("John Spring")).findAny().get();

        log.debug("User generated ID for the customer: {}", bootstrapData.manuallySetId);
        log.debug("Saved Customer's Hibernate Generated ID: {}", savedCustomer.getId());

        assertThat(bootstrapData.manuallySetId).isNotEqualTo(savedCustomer.getId());
    }
}
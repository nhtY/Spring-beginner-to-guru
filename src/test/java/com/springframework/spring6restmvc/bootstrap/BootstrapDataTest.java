package com.springframework.spring6restmvc.bootstrap;

import com.springframework.spring6restmvc.repositories.BeerRepository;
import com.springframework.spring6restmvc.repositories.CustomerRepository;
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

    BootstrapData bootstrapData;

    @BeforeEach
    void setUp() {
      bootstrapData = new BootstrapData(beerRepository, customerRepository);
    }

    @Test
    void run() throws Exception {
        // call the CommandLineRunner's run() method to initial data loading
        bootstrapData.run(null);

        // check if initial data loaded, we have 3 records per tables:
        assertThat(beerRepository.count()).isEqualTo(3);
        assertThat(customerRepository.count()).isEqualTo(3);
    }
}
package com.springframework.spring6restmvc.bootstrap;

import com.springframework.spring6restmvc.entities.Beer;
import com.springframework.spring6restmvc.model.BeerStyle;
import com.springframework.spring6restmvc.entities.Customer;
import com.springframework.spring6restmvc.repositories.BeerRepository;
import com.springframework.spring6restmvc.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class BootstrapData implements CommandLineRunner {

    private final BeerRepository beerRepository;
    private final CustomerRepository customerRepository;

    public UUID manuallySetId;

    @Override
    public void run(String... args) throws Exception {

        log.debug("Bootstrapping Data for H2...");

        // Create beers and save:
        loadBeerData();

        // Create customers and save:
        loadCustomerData();
    }

    private void loadBeerData() {

        if (beerRepository.count() == 0) {

            Beer beer1 = Beer.builder()
                    // Id and Version will be generated by Hibernate
                    .beerName("Galaxy Cat")
                    .beerStyle(BeerStyle.PALE_ALE)
                    .upc("123456")
                    .price(new BigDecimal("12.99"))
                    .quantityOnHand(122)
                    .createdDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .build();

            Beer beer2 = Beer.builder()
                    // Id and Version will be generated by Hibernate
                    .beerName("Crank")
                    .beerStyle(BeerStyle.PALE_ALE)
                    .upc("12332435")
                    .price(new BigDecimal("11.99"))
                    .quantityOnHand(392)
                    .createdDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .build();

            Beer beer3 = Beer.builder()
                    // Id and Version will be generated by Hibernate
                    .beerName("Sunshine City")
                    .beerStyle(BeerStyle.IPA)
                    .upc("123456")
                    .price(new BigDecimal("13.99"))
                    .quantityOnHand(144)
                    .createdDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .build();


            beerRepository.saveAll(Arrays.asList(beer1, beer2, beer3));
        }
    }

    private void loadCustomerData() {


        if (customerRepository.count() == 0) {

            Customer customer1 = Customer.builder()
                    .id(UUID.randomUUID()) // this ID will not be used. Hibernate generates and hibernate-generated value will be persisted.
                    .name("John Spring")
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .build();

            // will be used in test to see if this id will be persisted or hibernate generates its own id
            manuallySetId = customer1.getId();

            log.debug("Customer Name: {}, ID: {}", customer1.getName(), customer1.getId());

            Customer customer2 = Customer.builder()
                    // Id and Version will be generated by Hibernate
                    .name("Eva Blue")
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .build();

            Customer customer3 = Customer.builder()
                    // Id and Version will be generated by Hibernate
                    .name("Joseph Carter")
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .build();

            customerRepository.save(customer1);
            customerRepository.save(customer2);
            customerRepository.save(customer3);
        }

    }
}

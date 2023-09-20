package com.springframework.spring6restmvc.services;

import com.springframework.spring6restmvc.model.BeerCSVRecord;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BeerCsvServiceTest {

    BeerCsvService beerCsvService = new BeerCsvServiceImpl();

    @Test
    void convertCSV() throws FileNotFoundException {

        // get the csv file
        File file = ResourceUtils.getFile("classpath:csvdata/beers.csv");

        // convert each row to a BeerCSVRecord:
        List<BeerCSVRecord> recs = beerCsvService.convertCSV(file);

        // check if list size changed:
        System.out.println("Size of the List of BeerCSVRecords: " + recs.size());

        assertThat(recs.size()).isGreaterThan(0);
    }
}
package com.springframework.spring6restmvc.controller;

import com.springframework.spring6restmvc.model.Beer;
import com.springframework.spring6restmvc.services.BeerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@RestController
public class BeerController {
    private final BeerService beerService;

    // BECAUSE we use @AllArgsConstructor, we do not need to write the following constructor

//    public BeerController(BeerService beerService) {
//        this.beerService = beerService;
//    }

    @RequestMapping("/api/v1/beer")
    public List<Beer> listBeers() {
        return beerService.listBeers();
    }

    public Beer getBeerById(UUID id) {

        log.debug("Get Beer by Id - in controller...");

        return beerService.getBeerById(id);
    }

}

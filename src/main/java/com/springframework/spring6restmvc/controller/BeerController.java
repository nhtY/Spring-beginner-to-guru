package com.springframework.spring6restmvc.controller;

import com.springframework.spring6restmvc.model.Beer;
import com.springframework.spring6restmvc.services.BeerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Controller
public class BeerController {
    private final BeerService beerService;

    // BECAUSE we use @AllArgsConstructor, we do not need to write the following constructor

//    public BeerController(BeerService beerService) {
//        this.beerService = beerService;
//    }

    public Beer getBeerById(UUID id) {

        log.debug("Get Beer by Id - in controller...");

        return beerService.getBeerById(id);
    }

}

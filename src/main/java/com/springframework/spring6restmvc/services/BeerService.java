package com.springframework.spring6restmvc.services;

import com.springframework.spring6restmvc.model.Beer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeerService {
    List<Beer> listBeers();

    Beer saveNewBeer(Beer beer);

    Optional<Beer> getBeerById(UUID id);

    void updateById(UUID beerId, Beer beer);

    void deleteById(UUID beerId);

    void patchBeerById(UUID beerId, Beer beer);
}

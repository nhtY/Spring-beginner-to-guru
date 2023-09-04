package com.springframework.spring6restmvc.services;

import com.springframework.spring6restmvc.model.BeerDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeerService {
    List<BeerDTO> listBeers();

    BeerDTO saveNewBeer(BeerDTO beer);

    Optional<BeerDTO> getBeerById(UUID id);

    void updateById(UUID beerId, BeerDTO beer);

    void deleteById(UUID beerId);

    void patchBeerById(UUID beerId, BeerDTO beer);
}

package com.springframework.spring6restmvc.services;

import com.springframework.spring6restmvc.model.BeerDTO;
import com.springframework.spring6restmvc.model.BeerStyle;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeerService {
    List<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean isShowInventory, Integer pageNumber, Integer pageSize);

    BeerDTO saveNewBeer(BeerDTO beer);

    Optional<BeerDTO> getBeerById(UUID id);

    Optional<BeerDTO> updateById(UUID beerId, BeerDTO beer);

    Boolean deleteById(UUID beerId);

    Optional<BeerDTO> patchBeerById(UUID beerId, BeerDTO beer);
}

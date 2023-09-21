package com.springframework.spring6restmvc.services;

import com.springframework.spring6restmvc.entities.Beer;
import com.springframework.spring6restmvc.mappers.BeerMapper;
import com.springframework.spring6restmvc.model.BeerDTO;
import com.springframework.spring6restmvc.model.BeerStyle;
import com.springframework.spring6restmvc.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Primary // this implementation of the BeerService will be primarily used by spring
@RequiredArgsConstructor
public class BeerServiceJPA implements BeerService {
    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    @Override
    public List<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean isShowInventory, Integer pageNumber, Integer pageSize) {

        List<Beer> beerList;

        if (StringUtils.hasText(beerName) && beerStyle == null) {
            beerList = listBeerByName(beerName);
        } else if (!StringUtils.hasText(beerName) && beerStyle != null) {
            beerList = listBeerByStyle(beerStyle);
        } else if (StringUtils.hasText(beerName) && beerStyle != null) {
            beerList = listBeerByNameAndStyle(beerName, beerStyle);
        } else {
            beerList = beerRepository.findAll();
        }

        if (isShowInventory != null && !isShowInventory) {
            beerList.forEach(beer -> beer.setQuantityOnHand(null));
        }

        return beerList
                .stream()
                .map(beerMapper::beerToBeerDto)
                .collect(Collectors.toList());
    }

    List<Beer> listBeerByName(String beerName) {
        return beerRepository.findAllByBeerNameIsLikeIgnoreCase("%" + beerName + "%");
    }

    List<Beer> listBeerByStyle(BeerStyle style) {
        return beerRepository.findAllByBeerStyle(style);
    }

    List<Beer> listBeerByNameAndStyle(String beerName, BeerStyle style) {
        return beerRepository.findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle(
                "%" + beerName + "%", style);
    }

    @Override
    public BeerDTO saveNewBeer(BeerDTO beerDTO) {
        return beerMapper.beerToBeerDto(
                beerRepository.save(beerMapper.beerDtoToBeer(beerDTO))
        );
    }

    @Override
    public Optional<BeerDTO> getBeerById(UUID id) {
        return Optional.ofNullable(
                beerMapper.beerToBeerDto(beerRepository.findById(id).orElse(null))
        );
    }

    @Override
    public Optional<BeerDTO> updateById(UUID beerId, BeerDTO beer) {

        // cannot reach object created in stream, So, create a reference for it here and reach over reference
        AtomicReference<Optional<BeerDTO>> atomicReference = new AtomicReference<>();

        beerRepository.findById(beerId).ifPresentOrElse(foundBeer -> {
            foundBeer.setBeerName(beer.getBeerName());
            foundBeer.setBeerStyle(beer.getBeerStyle());
            foundBeer.setUpc(beer.getUpc());
            foundBeer.setPrice(beer.getPrice());

            atomicReference.set(Optional.of(
                    beerMapper
                            .beerToBeerDto(beerRepository.save(foundBeer))
            ));
        }, () -> {
            atomicReference.set(Optional.empty());
        });

        return atomicReference.get();
    }

    @Override
    public Boolean deleteById(UUID beerId) {
        if (beerRepository.existsById(beerId)) {
            beerRepository.deleteById(beerId);
            return true;
        }

        return false;
    }

    @Override
    public Optional<BeerDTO> patchBeerById(UUID beerId, BeerDTO beer) {
        AtomicReference<Optional<BeerDTO>> atomicReference = new AtomicReference<>();

        beerRepository.findById(beerId).ifPresentOrElse(existing -> {

            if (StringUtils.hasText(beer.getBeerName())) {
                existing.setBeerName(beer.getBeerName());
            }

            if (beer.getBeerStyle() != null) {
                existing.setBeerStyle(beer.getBeerStyle());
            }

            if (beer.getQuantityOnHand() != null) {
                existing.setQuantityOnHand(beer.getQuantityOnHand());
            }

            if (StringUtils.hasText(beer.getUpc())) {
                existing.setUpc(beer.getUpc());
            }

            if (beer.getPrice() != null) {
                existing.setPrice(beer.getPrice());
            }

            atomicReference.set(
                    Optional.of(
                          beerMapper.beerToBeerDto(beerRepository.save(existing))
                    )
            );

        }, () -> {
            atomicReference.set(Optional.empty());
        });

       return atomicReference.get();
    }
}

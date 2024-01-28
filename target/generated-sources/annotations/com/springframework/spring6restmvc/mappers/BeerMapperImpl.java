package com.springframework.spring6restmvc.mappers;

import com.springframework.spring6restmvc.entities.Beer;
import com.springframework.spring6restmvc.model.BeerDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-01-02T01:01:59+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.5 (Oracle Corporation)"
)
@Component
public class BeerMapperImpl implements BeerMapper {

    @Override
    public Beer beerDtoToBeer(BeerDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Beer.BeerBuilder beer = Beer.builder();

        beer.id( dto.getId() );
        beer.version( dto.getVersion() );
        beer.beerName( dto.getBeerName() );
        beer.beerStyle( dto.getBeerStyle() );
        beer.upc( dto.getUpc() );
        beer.quantityOnHand( dto.getQuantityOnHand() );
        beer.price( dto.getPrice() );
        beer.createdDate( dto.getCreatedDate() );
        beer.updateDate( dto.getUpdateDate() );

        return beer.build();
    }

    @Override
    public BeerDTO beerToBeerDto(Beer beer) {
        if ( beer == null ) {
            return null;
        }

        BeerDTO.BeerDTOBuilder beerDTO = BeerDTO.builder();

        beerDTO.id( beer.getId() );
        beerDTO.version( beer.getVersion() );
        beerDTO.beerName( beer.getBeerName() );
        beerDTO.beerStyle( beer.getBeerStyle() );
        beerDTO.upc( beer.getUpc() );
        beerDTO.quantityOnHand( beer.getQuantityOnHand() );
        beerDTO.price( beer.getPrice() );
        beerDTO.createdDate( beer.getCreatedDate() );
        beerDTO.updateDate( beer.getUpdateDate() );

        return beerDTO.build();
    }
}

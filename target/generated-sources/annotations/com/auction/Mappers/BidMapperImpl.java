package com.auction.Mappers;

import com.auction.Dtos.RequestBidDto;
import com.auction.Entity.Bid;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-06T18:43:57+0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 20.0.2 (Oracle Corporation)"
)
@Component
public class BidMapperImpl implements BidMapper {

    @Override
    public Bid toBid(RequestBidDto requestBidDto) {
        if ( requestBidDto == null ) {
            return null;
        }

        Bid bid = new Bid();

        bid.setAmount( requestBidDto.getAmount() );

        return bid;
    }
}

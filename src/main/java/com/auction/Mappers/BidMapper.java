package com.auction.Mappers;

import com.auction.Dtos.RequestBidDto;
import com.auction.Entity.Bid;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BidMapper {

    Bid toBid(RequestBidDto requestBidDto);
}

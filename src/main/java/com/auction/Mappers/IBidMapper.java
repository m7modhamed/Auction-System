package com.auction.Mappers;

import com.auction.Dtos.RequestBidDto;
import com.auction.Entity.Bid;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IBidMapper {

    Bid toBid(RequestBidDto requestBidDto);
}

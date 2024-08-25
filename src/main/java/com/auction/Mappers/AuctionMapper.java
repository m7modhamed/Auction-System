package com.auction.Mappers;

import com.auction.Dtos.RequestAuctionDto;
import com.auction.Dtos.ResponseAuctionDto;
import com.auction.Entity.Auction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuctionMapper {


    Auction toAuction(RequestAuctionDto auction);


    ResponseAuctionDto toResponseAuctionDto(Auction auction);


}

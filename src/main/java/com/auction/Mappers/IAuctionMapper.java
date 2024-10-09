package com.auction.Mappers;

import com.auction.Dtos.GetAuctionDto;
import com.auction.Dtos.RequestAuctionDto;
import com.auction.Dtos.ResponseAuctionDto;
import com.auction.Entity.Auction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IAuctionMapper {


    Auction toAuction(RequestAuctionDto auction);


    ResponseAuctionDto toResponseAuctionDto(Auction auction);

    GetAuctionDto toGetAuctionDto(Auction auction);

}

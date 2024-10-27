package com.auction.auctionmanagement.Mapper;

import com.auction.auctionmanagement.dto.GetAuctionDto;
import com.auction.auctionmanagement.dto.RequestAuctionDto;
import com.auction.auctionmanagement.dto.ResponseAuctionDto;
import com.auction.auctionmanagement.model.Auction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IAuctionMapper {


    Auction toAuction(RequestAuctionDto auction);


    ResponseAuctionDto toResponseAuctionDto(Auction auction);

    GetAuctionDto toGetAuctionDto(Auction auction);

}

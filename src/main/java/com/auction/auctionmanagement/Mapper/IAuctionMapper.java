package com.auction.auctionmanagement.Mapper;

import com.auction.auctionmanagement.dto.AbstractionAuctionInfoDto;
import com.auction.auctionmanagement.dto.GetAuctionDto;
import com.auction.auctionmanagement.dto.RequestAuctionDto;
import com.auction.auctionmanagement.dto.ResponseAuctionDto;
import com.auction.auctionmanagement.model.Auction;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface IAuctionMapper {


    Auction toAuction(RequestAuctionDto auction);


    ResponseAuctionDto toResponseAuctionDto(Auction auction);
    List<ResponseAuctionDto> toResponseAuctionDtos(List<Auction> auctions);

    GetAuctionDto toGetAuctionDto(Auction auction);

    AbstractionAuctionInfoDto toAbstractionAuctionInfoDto(Auction auction);
}

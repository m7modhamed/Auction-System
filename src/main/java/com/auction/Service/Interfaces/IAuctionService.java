package com.auction.Service.Interfaces;

import com.auction.Dtos.RequestAuctionDto;
import com.auction.Entity.Auction;

import java.util.List;
import java.util.Optional;

public interface IAuctionService {

    Auction CreateAuction(RequestAuctionDto requestAuctionDto, Long userId);

    List<Auction> getAllAuctions();

    Optional<Auction> getAuctionById(Long auctionId);
}

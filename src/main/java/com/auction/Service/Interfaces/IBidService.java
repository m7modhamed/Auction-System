package com.auction.Service.Interfaces;

import com.auction.Dtos.RequestBidDto;
import com.auction.Entity.Auction;
import com.auction.Entity.Bid;

public interface IBidService {
    Auction makeBid(RequestBidDto requestBidDto, Long userId);

    void deleteBidById(Long bidId, Long userId);

    Bid getLatestBid(Auction auction);
}

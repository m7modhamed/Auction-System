package com.auction.auctionmanagement.service.interfaces;

import com.auction.auctionmanagement.dto.RequestBidDto;
import com.auction.auctionmanagement.enums.BidStatus;
import com.auction.auctionmanagement.model.Auction;
import com.auction.auctionmanagement.model.Bid;
import java.util.List;

public interface IBidService {
    Auction createNewBid(RequestBidDto requestBidDto, Long userId);

    void deleteBidById(Long bidId, Long userId);

    Bid getLatestBid(Auction auction);

    Bid save(Bid bid);

    List<Bid> findAllByStatusAndAuctionId(BidStatus status, long auctionId);
}

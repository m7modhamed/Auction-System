package com.auction.auctionmanagement.repository;

import com.auction.auctionmanagement.enums.BidStatus;
import com.auction.auctionmanagement.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid,Long> {


    List<Bid> findAllByStatusAndAuctionId(BidStatus status, long auctionId);

}

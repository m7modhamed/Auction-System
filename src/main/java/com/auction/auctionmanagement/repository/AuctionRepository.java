package com.auction.auctionmanagement.repository;

import com.auction.auctionmanagement.enums.AuctionStatus;
import com.auction.auctionmanagement.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuctionRepository  extends JpaRepository<Auction,Long> , JpaSpecificationExecutor<Auction> {

    List<Auction> findByStatus(AuctionStatus status);

}

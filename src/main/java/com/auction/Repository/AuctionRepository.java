package com.auction.Repository;

import com.auction.Entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionRepository  extends JpaRepository<Auction,Long> {

    List<Auction> findByActiveTrue();

}

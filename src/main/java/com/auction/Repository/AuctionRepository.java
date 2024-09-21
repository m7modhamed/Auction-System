package com.auction.Repository;

import com.auction.Entity.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionRepository  extends JpaRepository<Auction,Long> , JpaSpecificationExecutor<Auction> {

    Page<Auction> findByActiveTrue(Pageable pageable);
    List<Auction> findByActiveTrue();



}

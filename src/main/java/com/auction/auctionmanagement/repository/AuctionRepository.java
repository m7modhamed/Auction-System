package com.auction.auctionmanagement.repository;

import com.auction.auctionmanagement.enums.AuctionStatus;
import com.auction.auctionmanagement.model.Auction;
import com.auction.usersmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuctionRepository  extends JpaRepository<Auction,Long> , JpaSpecificationExecutor<Auction> {

    List<Auction> findByStatus(AuctionStatus status);

    @Query("SELECT a FROM Auction a JOIN a.participants u WHERE u.id = :userId")
    List<Auction> findAuctionsJoinedByUser(@Param("userId") long userId);


    List<Auction> findAllBySellerId(long sellerId);

    List<Auction> findAllByWinnerId(long winnerId);


}

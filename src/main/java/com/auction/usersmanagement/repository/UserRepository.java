package com.auction.usersmanagement.repository;

import com.auction.usersmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u JOIN u.JoinedAuctions a WHERE a.id = :auctionId")
    List<User> findAllByAuctionId(@Param("auctionId") long auctionId);
}

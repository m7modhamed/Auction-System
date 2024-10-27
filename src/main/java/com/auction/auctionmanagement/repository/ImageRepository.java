package com.auction.auctionmanagement.repository;

import com.auction.auctionmanagement.model.AuctionImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<AuctionImage,Long> {


    Optional<AuctionImage> findByName(String imagePath);
}

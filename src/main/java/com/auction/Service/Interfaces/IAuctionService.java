package com.auction.Service.Interfaces;

import com.auction.Dtos.RequestAuctionDto;
import com.auction.Entity.Auction;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IAuctionService {

    Auction CreateAuction(RequestAuctionDto requestAuctionDto,Long userId);

    List<Auction> getAllAuctions();

    Optional<Auction> getAuctionById(Long auctionId);

    void deleteAuctionById(Long id, Long userId);

    List<Auction> getMyAuctions(Long userId);

    List<Auction> getMyWonAuctions(Long userId);
    List<Auction> getActiveAuctions();

}

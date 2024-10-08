package com.auction.Service.Interfaces;

import com.auction.Dtos.AuctionSearchCriteria;
import com.auction.Dtos.RequestAuctionDto;
import com.auction.Entity.Auction;
import com.auction.Entity.User;
import com.auction.utility.Utility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IAuctionService {

    Auction CreateAuction(RequestAuctionDto requestAuctionDto,Long userId);

    List<Auction> getAllAuctions();

    Auction getAuctionById(Long auctionId);

    void deleteAuctionById(Long id, Long userId);

    List<Auction> getMyAuctions(Long userId);

    List<Auction> getMyWonAuctions(Long userId);
    Page<Auction> getActiveAuctions(AuctionSearchCriteria criteria, PageRequest pageRequest);

    List<Auction> getActiveAuctions();

    void joinAuction(long auctionId);

    boolean canDeleteWithoutCharge(Long accountId);
}

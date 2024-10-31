package com.auction.auctionmanagement.service.interfaces;

import com.auction.auctionmanagement.dto.AuctionSearchCriteria;
import com.auction.auctionmanagement.dto.GetAuctionDto;
import com.auction.auctionmanagement.dto.RequestAuctionDto;
import com.auction.auctionmanagement.enums.AuctionStatus;
import com.auction.auctionmanagement.model.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Set;

public interface IAuctionService {

    Auction CreateAuction(RequestAuctionDto requestAuctionDto,Long userId) ;

    Auction getAuctionById(Long auctionId);
    GetAuctionDto getAuctionDtoById(Long auctionId);
    void deleteAuctionById(Long id, Long userId);

    List<Auction> getMyAuctions(Long userId);

    List<Auction> getMyWonAuctions(Long userId);
    Page<Auction> getActiveAuctions(AuctionSearchCriteria criteria, PageRequest pageRequest);

    List<Auction> getAuctionByStatus(AuctionStatus status);

    void joinAuction(long auctionId);

    boolean canDeleteWithoutCharge(Long accountId);

    void receiveAuctionItem(Long auctionId);

    void returnReservedAmountToBidders(Auction auction);

    List<Auction> getUserJoinedAuctions(Long accountId);
}

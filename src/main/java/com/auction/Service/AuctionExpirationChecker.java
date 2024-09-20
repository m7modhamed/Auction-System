package com.auction.Service;

import com.auction.Entity.Auction;
import com.auction.Entity.Bid;
import com.auction.Repository.AuctionRepository;
import com.auction.Service.Implementation.BidService;
import com.auction.Service.Interfaces.IAuctionService;
import com.auction.Entity.User;
import com.auction.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuctionExpirationChecker {

    private static final double COMMISSION_PERCENTAGE=0.1;

    private final IAuctionService auctionService;
    private final BidService bidService;
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;



    // Scheduled task to check for expired auctions every minute
    @Scheduled(fixedRate = 60000) // 60,000 milliseconds = 1 minute
    @Transactional
    public void checkExpiredAuctions() {
        // Get a list of active auctions that have not yet expired
        List<Auction> activeAuctions = auctionService.getActiveAuctions();

        for (Auction auction : activeAuctions) {
            // Check if the current time has surpassed the expiry date of the auction
            if (LocalDateTime.now().isAfter(auction.getExpireDate())) {
                // Auction has expired, handle the logic for expired auctions
                handleExpiredAuction(auction);
            }
        }
    }



    // Method to handle the logic for an expired auction
    @Transactional
    public void handleExpiredAuction(Auction auction) {

        // retrieve the highest bid for an auction
        if(auction.getBids() == null || auction.getBids().isEmpty()) {
            auction.setActive(false);
            return ;
        }

        //get latest Bid of the auction
        Bid latestBid = bidService.getLatestBid(auction);

        // Determine the winner
        User winner = latestBid.getBidder();

        winner.getWonAuctions().add(auction);


        // Determine the commission
        double commission = latestBid.getAmount() * COMMISSION_PERCENTAGE;
        auction.setCommission(commission);

        auction.setActive(false);

        userRepository.save(winner);
        auctionRepository.save(auction);

    }

}

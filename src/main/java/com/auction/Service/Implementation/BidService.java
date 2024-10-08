package com.auction.Service.Implementation;

import com.auction.Dtos.RequestBidDto;
import com.auction.Entity.Auction;
import com.auction.Entity.Bid;
import com.auction.Repository.BidRepository;
import com.auction.Service.Interfaces.IBidService;
import com.auction.exceptions.AppException;
import com.auction.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BidService implements IBidService {
    private final BidRepository bidRepository;
    private final AuctionService auctionService;
    private final AccountService accountService;

    @Override
    public Auction makeBid(RequestBidDto requestBidDto, Long userId) {
        Long auctionId = requestBidDto.getAuctionId();
        double bidAmount = requestBidDto.getAmount();

        Bid bid = new Bid();

        //check if the bidder exist or not && set bidder
        Optional<User> bidder = accountService.getUserById(userId);
        if (bidder.isEmpty()) {
            throw new AppException("User Not Found", HttpStatus.NOT_FOUND);
        }

        bid.setBidder(bidder.get());


        Auction auction = auctionService.getAuctionById(auctionId);

        if (!auction.getActive()) {
            throw new AppException("Auction Not Active", HttpStatus.BAD_REQUEST);
        }
        bid.setAuction(auction);


        if (Objects.equals(userId, auction.getSeller().getId())) {
            throw new AppException("You Are Auction Owner", HttpStatus.BAD_REQUEST);
        }

        //check amount of bid > minimum bid allowed
        if (bidAmount < auction.getMinBid()) {
            throw new AppException("Bid Amount Must Greater Than The Minimum Of Bid", HttpStatus.BAD_REQUEST);
        }

        //check amount of bid > Top bid
        Bid topBid=getLatestBid(auction);
        if (bidAmount < topBid.getAmount()) {
            throw new AppException("Bid Amount Must Greater Than The Top Bid", HttpStatus.BAD_REQUEST);
        }
        bid.setAmount(bidAmount);
        auction.setCurrentPrice(auction.getCurrentPrice() + bidAmount);
        auction.getBids().add(bid);
        bidRepository.save(bid);

        return bid.getAuction();
    }

    @Override
    public void deleteBidById(Long bidId, Long userId) {
        Optional<User> user = accountService.getUserById(userId);
        Optional<Bid> bid = bidRepository.findById(bidId);

        if (user.isEmpty()) {
            throw new AppException("User Not Found", HttpStatus.NOT_FOUND);
        }
        if (bid.isEmpty()) {
            throw new AppException("Bid Not Found", HttpStatus.NOT_FOUND);
        }


        if(!bid.get().getBidder().equals(user.get())){
            throw new AppException("You are not the owner of the bid.", HttpStatus.NOT_FOUND);

        }



        //charge the user wanted to delete her/his bid
        /*
         *
         *
         * */


        bidRepository.deleteById(bidId);
    }

    @Override
    public Bid getLatestBid(Auction auction) {
        List<Bid> bids= auction.getBids();

        // Sort the bids by bid time in descending order
        bids.sort(Comparator.comparing(Bid::getBidTime).reversed());

        // Return the first bid in the sorted list, which is the latest bid
        return bids.get(0);

    }
}

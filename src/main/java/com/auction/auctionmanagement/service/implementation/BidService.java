package com.auction.auctionmanagement.service.implementation;

import com.auction.auctionmanagement.dto.RequestBidDto;
import com.auction.auctionmanagement.enums.AuctionStatus;
import com.auction.auctionmanagement.enums.BidStatus;
import com.auction.auctionmanagement.model.Auction;
import com.auction.auctionmanagement.model.Bid;
import com.auction.auctionmanagement.repository.BidRepository;
import com.auction.auctionmanagement.service.interfaces.IAuctionService;
import com.auction.auctionmanagement.service.interfaces.IBidService;
import com.auction.paymentmanagement.enums.TransactionType;
import com.auction.paymentmanagement.model.Transaction;
import com.auction.paymentmanagement.service.interfaces.IPaymentService;
import com.auction.usersmanagement.service.interfaces.IUserService;
import com.auction.common.exceptions.AppException;
import com.auction.usersmanagement.model.User;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Transactional
public class BidService implements IBidService {
    private final BidRepository bidRepository;
    private final IAuctionService auctionService;
    private final IUserService userService;
    private final IPaymentService paymentService;
    private static final int DELETION_TIME_LIMIT =20;


    @Override
    public Auction createNewBid(RequestBidDto requestBidDto, Long userId) {
        Long auctionId = requestBidDto.getAuctionId();
        double bidAmount = requestBidDto.getAmount();

        Bid bid = new Bid();

        //check if the bidder exist or not && set bidder
        User bidder = userService.getUserById(userId);

        bid.setBidder(bidder);

        Auction auction = auctionService.getAuctionById(auctionId);

        if (Objects.equals(userId, auction.getSeller().getId())) {
            throw new AppException("You Are Auction Owner", HttpStatus.BAD_REQUEST);
        }

        if(!bidder.getJoinedAuctions().contains(auction)){
            throw new AppException("You need to join the auction before placing a bid.", HttpStatus.BAD_REQUEST);
        }

        if (auction.getStatus() != AuctionStatus.ACTIVE)  {
            throw new AppException("Auction Not Active", HttpStatus.BAD_REQUEST);
        }
        bid.setAuction(auction);




        //check amount of bid > minimum bid allowed
        double  allowedAmount = auction.getMinBid() + auction.getCurrentPrice();
        if (bidAmount < allowedAmount) {
            throw new AppException("Bid Amount Must Greater Than the amount : " + allowedAmount, HttpStatus.BAD_REQUEST);
        }


        bid.setStatus(BidStatus.ACTIVE);
        bid.setAmount(bidAmount);
        auction.setCurrentPrice(bidAmount);
        auction.getBids().add(bid);
        bidRepository.save(bid);

        return bid.getAuction();
    }

    @Override
    public void deleteBidById(Long bidId, Long userId) {
        User user = userService.getUserById(userId);
        Bid bid = getBidById(bidId);
        Auction auction=bid.getAuction();


        if(!bid.getBidder().equals(user)){
            throw new AppException("You are not the owner of the bid.", HttpStatus.NOT_FOUND);
        } else if (bid.getStatus() != BidStatus.ACTIVE) {
            throw new AppException("Bid is not active and cannot be processed.", HttpStatus.NOT_FOUND);
        }

        bid.setStatus(BidStatus.CANCELLED);

        //check the time before deletion
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime deletionAllowedTime = bid.getBidTime().plusMinutes(DELETION_TIME_LIMIT);
        Transaction requiredTransaction=null;

        for(Transaction transaction: bid.getBidder().getPaymentAccount().getPaymentMethod().getTransactions()){
            if(transaction.getAuction().equals(auction) && transaction.getType() == TransactionType.JOIN_AUCTION){
                requiredTransaction=transaction;
            }
        }

        if(currentTime.isAfter(deletionAllowedTime)){
            //here we don't need to refund the reserved amount to the user account
            try {
                paymentService.capturePaymentIntent(requiredTransaction);
            } catch (StripeException e) {
                throw new AppException(e.getMessage() , HttpStatus.BAD_REQUEST);
            }
        }

        bid.setStatus(BidStatus.CANCELLED);

       // List<Bid> bids =auction.getBids();
        List<Bid> bids =  bidRepository.findAllByStatusAndAuctionId(BidStatus.ACTIVE , auction.getId());
        bids.sort(Comparator.comparing(Bid::getBidTime).reversed());
        boolean isLatestBid = true;
        if(!bids.isEmpty()){
            isLatestBid= !bid.getBidTime().isBefore(bids.get(0).getBidTime());
        }

        if(isLatestBid){
            if(!bids.isEmpty()) {
                auction.setCurrentPrice(bids.get(0).getAmount());
            }else{
                auction.setCurrentPrice(auction.getInitialPrice());
            }
        }
        bidRepository.save(bid);
    }

    private Bid getBidById(Long bidId) {
        return bidRepository.findById(bidId).orElseThrow(
                () -> new AppException("Bid Not Found", HttpStatus.NOT_FOUND)
        );
    }


    @Override
    public Bid getLatestBid(Auction auction) {
        List<Bid> bids= findAllByStatusAndAuctionId(BidStatus.ACTIVE , auction.getId());

        if(bids == null || bids.isEmpty()){
            return null;
        }
        // Sort the bids by bid time in descending order
        bids.sort(Comparator.comparing(Bid::getBidTime).reversed());

        // Return the first bid in the sorted list, which is the latest bid
        return bids.get(0);

    }

    @Override
    public Bid save(Bid bid) {
        return bidRepository.save(bid);
    }

    @Override
    public List<Bid> findAllByStatusAndAuctionId(BidStatus status, long auctionId) {
        return bidRepository.findAllByStatusAndAuctionId(status , auctionId);
    }
}

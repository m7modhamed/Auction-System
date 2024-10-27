package com.auction.auctionmanagement.service;

import com.auction.auctionmanagement.enums.AuctionStatus;
import com.auction.auctionmanagement.enums.BidStatus;
import com.auction.auctionmanagement.model.Auction;
import com.auction.auctionmanagement.model.Bid;
import com.auction.auctionmanagement.repository.AuctionRepository;
import com.auction.auctionmanagement.service.implementation.BidService;
import com.auction.auctionmanagement.service.interfaces.IAuctionService;
import com.auction.common.exceptions.AppException;
import com.auction.paymentmanagement.enums.TransactionType;
import com.auction.paymentmanagement.model.Transaction;
import com.auction.paymentmanagement.service.interfaces.IPaymentService;
import com.auction.usersmanagement.model.User;
import com.auction.usersmanagement.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuctionExpirationChecker {


    private final IAuctionService auctionService;
    private final BidService bidService;
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final IPaymentService paymentService;


    // Scheduled task to check for expired auctions every minute
    @Scheduled(fixedRate = 60000) // 60,000 milliseconds = 1 minute
    @Transactional
    public void checkExpiredAuctions() {
        // Get a list of active auctions that have not yet expired
        List<Auction> activeAuctions = auctionService.getAuctionByStatus(AuctionStatus.ACTIVE);

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
        Transaction requiredTransaction=null;

        List<Bid> bids = bidService.findAllByStatusAndAuctionId(BidStatus.ACTIVE , auction.getId());

        List<Transaction> sellerTransactions = auction.getSeller().getPaymentAccount().getPaymentMethod().getTransactions();

        for(Transaction transaction: sellerTransactions){
            if(transaction.getAuction().equals(auction) && transaction.getType() == TransactionType.CREATE_AUCTION){
                requiredTransaction=transaction;
                break;
            }
        }
        if(bids == null || bids.isEmpty()) {
            try {
                paymentService.cancelPaymentIntent(requiredTransaction);
            } catch (StripeException e) {
                throw new AppException(e.getMessage(), HttpStatus.BAD_REQUEST);
            }

            auction.setStatus(AuctionStatus.COMPLETED);

            auctionRepository.save(auction);
            return;
        }
        double auctionCommission ;
        try {
            PaymentIntent commissionpaymentIntent = paymentService.capturePaymentIntent(requiredTransaction);
            auctionCommission = (double) commissionpaymentIntent.getAmount() / 100;
        } catch (StripeException e) {
            throw new AppException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }


        //get latest Bid of the auction
        Bid latestBid = bidService.getLatestBid(auction);

        // Determine the winner
        User winner = latestBid.getBidder();

        //here we must charge winner the auction price - reserved amount
        Long auctionPriceInCent = (long)latestBid.getAmount() * 100;
        try {
            paymentService.createPaymentIntent(auction,winner , auctionPriceInCent , TransactionType.PAY_AUCTION , PaymentIntentCreateParams.CaptureMethod.AUTOMATIC);
        } catch (StripeException e) {
            throw new AppException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }


        auctionService.returnReservedAmountToBidders(auction);

        auction.setCommission(auctionCommission);

        auction.setStatus(AuctionStatus.TIME_OUT);

        latestBid.setStatus(BidStatus.WON);
        bidService.save(latestBid);

        winner.getWonAuctions().add(auction);
        userRepository.save(winner);
        auctionRepository.save(auction);
    }

}

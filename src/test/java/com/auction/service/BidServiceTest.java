package com.auction.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.auction.auctionmanagement.dto.RequestBidDto;
import com.auction.auctionmanagement.enums.AuctionStatus;
import com.auction.auctionmanagement.enums.BidStatus;
import com.auction.auctionmanagement.model.Auction;
import com.auction.auctionmanagement.model.Bid;
import com.auction.auctionmanagement.repository.BidRepository;
import com.auction.auctionmanagement.service.implementation.BidService;
import com.auction.auctionmanagement.service.interfaces.IAuctionService;
import com.auction.common.exceptions.AppException;
import com.auction.paymentmanagement.service.interfaces.IPaymentService;
import com.auction.usersmanagement.model.User;
import com.auction.usersmanagement.service.interfaces.IUserService;

public class BidServiceTest {

    @InjectMocks
    private BidService bidService;

    @Mock
    private BidRepository bidRepository;

    @Mock
    private IAuctionService auctionService;

    @Mock
    private IUserService userService;

    @Mock
    private IPaymentService paymentService;

    private User user;
    private Auction auction;
    private Bid bid;
    private RequestBidDto requestBidDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up mock data
        user = new User();
        user.setId(1L);
        
        auction = new Auction();
        auction.setId(1L);
        auction.setStatus(AuctionStatus.ACTIVE);
        auction.setSeller(user);
        auction.setMinBid((float) 10.0);
        auction.setCurrentPrice(20.0);
        
        bid = new Bid();
        bid.setBidder(user);
        bid.setAuction(auction);
        bid.setAmount(30.0);
        bid.setStatus(BidStatus.ACTIVE);
        bid.setBidTime(LocalDateTime.now().minusMinutes(5));

        requestBidDto = new RequestBidDto();
        requestBidDto.setAuctionId(1L);
        requestBidDto.setAmount(30.0);
    }

    @Test
    public void testCreateNewBid_success() {
        when(userService.getUserById(1L)).thenReturn(user);
        when(auctionService.getAuctionById(1L)).thenReturn(auction);
        when(bidRepository.save(any(Bid.class))).thenReturn(bid);

        Auction result = bidService.createNewBid(requestBidDto, 1L);

        assertNotNull(result);
        assertEquals(auction, result);
        verify(bidRepository, times(1)).save(any(Bid.class));
    }

    @Test
    public void testCreateNewBid_auctionNotActive() {
        auction.setStatus(AuctionStatus.COMPLETED);

        when(userService.getUserById(1L)).thenReturn(user);
        when(auctionService.getAuctionById(1L)).thenReturn(auction);

        AppException exception = assertThrows(AppException.class, () -> {
            bidService.createNewBid(requestBidDto, 1L);
        });

        assertEquals("Auction Not Active", exception.getMessage());
    }

    @Test
    public void testCreateNewBid_bidAmountTooLow() {
        requestBidDto.setAmount(15.0);

        when(userService.getUserById(1L)).thenReturn(user);
        when(auctionService.getAuctionById(1L)).thenReturn(auction);

        AppException exception = assertThrows(AppException.class, () -> {
            bidService.createNewBid(requestBidDto, 1L);
        });

        assertEquals("Bid Amount Must Greater Than the amount : 30.0", exception.getMessage());
    }

    @Test
    public void testCanDeleteBidWithoutCharge() {
        when(bidRepository.findById(1L)).thenReturn(Optional.of(bid));

        boolean result = bidService.canDeleteBidWithoutCharge(1L);

        assertTrue(result);
    }

    @Test
    public void testDeleteBidById_success() {
        when(userService.getUserById(1L)).thenReturn(user);
        when(bidRepository.findById(1L)).thenReturn(Optional.of(bid));
        when(auctionService.getAuctionById(1L)).thenReturn(auction);
        when(bidRepository.save(any(Bid.class))).thenReturn(bid);

        bidService.deleteBidById(1L, 1L);

        assertEquals(BidStatus.CANCELLED, bid.getStatus());
        verify(bidRepository, times(1)).save(any(Bid.class));
    }

    @Test
    public void testDeleteBidById_notBidOwner() {
        User otherUser = new User();
        otherUser.setId(2L);
        when(userService.getUserById(2L)).thenReturn(otherUser);
        when(bidRepository.findById(1L)).thenReturn(Optional.of(bid));

        AppException exception = assertThrows(AppException.class, () -> {
            bidService.deleteBidById(1L, 2L);
        });

        assertEquals("You are not the owner of the bid.", exception.getMessage());
    }

    @Test
    public void testGetLatestBid() {
        when(bidRepository.findAllByStatusAndAuctionId(BidStatus.ACTIVE, 1L)).thenReturn(Collections.singletonList(bid));

        Bid result = bidService.getLatestBid(auction);

        assertNotNull(result);
        assertEquals(bid, result);
    }
}

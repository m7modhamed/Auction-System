package com.auction.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.auction.auctionmanagement.Mapper.IAuctionMapper;
import com.auction.auctionmanagement.dto.RequestAuctionDto;
import com.auction.auctionmanagement.enums.AuctionStatus;
import com.auction.auctionmanagement.model.Auction;
import com.auction.auctionmanagement.repository.AuctionRepository;
import com.auction.auctionmanagement.service.implementation.AuctionService;
import com.auction.common.exceptions.AppException;
import com.auction.paymentmanagement.model.PaymentAccount;
import com.auction.paymentmanagement.service.interfaces.IPaymentService;
import com.auction.usersmanagement.model.User;
import com.auction.usersmanagement.service.interfaces.IAccountService;
import com.auction.usersmanagement.service.interfaces.IUserService;
import com.stripe.exception.StripeException;

class AuctionServiceTest {

    @Mock
    private IAuctionMapper auctionMapper;

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private IAccountService accountService;

    @Mock
    private IUserService userService;

    @Mock
    private IPaymentService paymentService;

    @InjectMocks
    private AuctionService auctionService;

    private User mockUser;
    private Auction mockAuction;
    private RequestAuctionDto requestAuctionDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup mock user
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setPaymentAccount(new PaymentAccount());

        // Setup mock auction
        mockAuction = new Auction();
        mockAuction.setId(1L);
        mockAuction.setBeginDate(LocalDateTime.now());

        // Setup mock request auction DTO
        requestAuctionDto = new RequestAuctionDto();
    }

    @Test
    void testCreateAuction_Success() throws StripeException {
        // Arrange
        when(auctionMapper.toAuction(requestAuctionDto)).thenReturn(mockAuction);
        when(accountService.getAccountById(1L)).thenReturn(mockUser);
        when(auctionRepository.save(mockAuction)).thenReturn(mockAuction);
        when(paymentService.createPaymentIntent(any(), any(), anyLong(), any(), any())).thenReturn(null);

        // Act
        Auction result = auctionService.CreateAuction(requestAuctionDto, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(auctionRepository).save(mockAuction);
    }

    @Test
    void testCreateAuction_Fail_ExpirationTime() {
        // Arrange
        requestAuctionDto.getExpireDate().isBefore(LocalDateTime.now().plusHours(1));
        
        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            auctionService.CreateAuction(requestAuctionDto, 1L);
        });
        assertEquals("The expiration time must be at least after 1 hour from now", exception.getMessage());
    }

    @Test
    void testGetAuctionById_Success() {
        // Arrange
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(mockAuction));

        // Act
        Auction result = auctionService.getAuctionById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetAuctionById_NotFound() {
        // Arrange
        when(auctionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            auctionService.getAuctionById(1L);
        });
        assertEquals("Auction not found", exception.getMessage());
    }

    @Test
    void testJoinAuction_Success() throws StripeException {
        // Arrange
        when(accountService.getAccountById(1L)).thenReturn(mockUser);
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(mockAuction));
        when(paymentService.createPaymentIntent(any(), any(), anyLong(), any(), any())).thenReturn(null);

        // Act
        auctionService.joinAuction(1L);

        // Assert
        verify(paymentService).createPaymentIntent(any(), any(), anyLong(), any(), any());
    }

    @Test
    void testJoinAuction_Fail_ExpiredAuction() {
        // Arrange
        mockAuction.setStatus(AuctionStatus.CANCELLED); // Make auction expired
        when(accountService.getAccountById(1L)).thenReturn(mockUser);
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(mockAuction));

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            auctionService.joinAuction(1L);
        });
        assertEquals("You are attempting to join an expired auction.", exception.getMessage());
    }

    @Test
    void testDeleteAuction_Success() throws StripeException {
        // Arrange
        mockAuction.setStatus(AuctionStatus.ACTIVE);
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(mockAuction));
        when(userService.getUserById(1L)).thenReturn(mockUser);
        when(paymentService.cancelPaymentIntent(any())).thenReturn(null);
        when(paymentService.capturePaymentIntent(any())).thenReturn(null);

        // Act
        auctionService.deleteAuctionById(1L, 1L);

        // Assert
        verify(auctionRepository).save(mockAuction);
    }

    @Test
    void testDeleteAuction_Fail_NotOwner() {
        // Arrange
        mockAuction.setStatus(AuctionStatus.ACTIVE);
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(mockAuction));
        when(userService.getUserById(2L)).thenReturn(mockUser);

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            auctionService.deleteAuctionById(1L, 2L);
        });
        assertEquals("Deletion failed. The user attempting to delete the auction is not the owner.", exception.getMessage());
    }
}

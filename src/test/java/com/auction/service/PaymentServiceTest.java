package com.auction.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.auction.auctionmanagement.model.Auction;
import com.auction.paymentmanagement.enums.TransactionType;
import com.auction.paymentmanagement.model.PaymentAccount;
import com.auction.paymentmanagement.model.Transaction;
import com.auction.paymentmanagement.service.implementation.PaymentService;
import com.auction.paymentmanagement.service.implementation.TransactionService;
import com.auction.usersmanagement.model.SysAccount;
import com.auction.usersmanagement.model.User;
import com.auction.usersmanagement.service.implementation.UserService;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Transfer;
import com.stripe.param.PaymentIntentCreateParams;

public class PaymentServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 1. Test createExpressAccountForUser
    @Test
    void testCreateExpressAccountForUser() throws StripeException {
        User user = new User();
        user.setEmail("user@example.com");
        when(userService.save(any(User.class))).thenReturn(user);

        String accountLink = paymentService.createExpressAccountForUser(user);

        assertNotNull(accountLink);
        verify(userService).save(user); // Ensure user is saved with Stripe account ID
    }

    // 2. Test createCustomer
    @Test
    void testCreateCustomer() throws StripeException {
        SysAccount sysAccount = new SysAccount();
        sysAccount.setFirstName("John");
        sysAccount.setLastName("Doe");
        sysAccount.setEmail("john.doe@example.com");

        Customer customer = paymentService.createCustomer(sysAccount);

        assertNotNull(customer);
        assertEquals("john.doe@example.com", customer.getEmail());
    }

    // 3. Test createPaymentIntent for CREATE_AUCTION
    @Test
    void testCreatePaymentIntentForCreateAuction() throws StripeException {
        User user = new User();
        PaymentAccount paymentAccount = new PaymentAccount();
        paymentAccount.setCustomerId("cus_12345");
        user.setPaymentAccount(paymentAccount);

        TransactionType type = TransactionType.CREATE_AUCTION;
        long amount = 10000L;

        Auction auction = new Auction();
        auction.setId(1L);

        when(transactionService.saveTransaction(any(Transaction.class))).thenReturn(new Transaction());

        PaymentIntent paymentIntent = paymentService.createPaymentIntent(auction, user, amount, type, PaymentIntentCreateParams.CaptureMethod.AUTOMATIC);

        assertNotNull(paymentIntent);
        assertEquals("usd", paymentIntent.getCurrency());
        verify(transactionService).saveTransaction(any(Transaction.class)); // Verify the transaction is saved
    }

    // 4. Test createPaymentIntent for JOIN_AUCTION
    @Test
    void testCreatePaymentIntentForJoinAuction() throws StripeException {
        User user = new User();
        PaymentAccount paymentAccount = new PaymentAccount();
        paymentAccount.setCustomerId("cus_12345");
        user.setPaymentAccount(paymentAccount);

        TransactionType type = TransactionType.JOIN_AUCTION;
        long amount = 5000L;

        Auction auction = new Auction();
        auction.setId(2L);

        when(transactionService.saveTransaction(any(Transaction.class))).thenReturn(new Transaction());

        PaymentIntent paymentIntent = paymentService.createPaymentIntent(auction, user, amount, type, PaymentIntentCreateParams.CaptureMethod.AUTOMATIC);

        assertNotNull(paymentIntent);
        assertEquals("usd", paymentIntent.getCurrency());
        verify(transactionService).saveTransaction(any(Transaction.class)); // Verify the transaction is saved
    }

    // 5. Test createPaymentIntent for PAY_AUCTION
    @Test
    void testCreatePaymentIntentForPayAuction() throws StripeException {
        User user = new User();
        PaymentAccount paymentAccount = new PaymentAccount();
        paymentAccount.setCustomerId("cus_12345");
        user.setPaymentAccount(paymentAccount);

        TransactionType type = TransactionType.PAY_AUCTION;
        long amount = 20000L;

        Auction auction = new Auction();
        auction.setId(3L);

        when(transactionService.saveTransaction(any(Transaction.class))).thenReturn(new Transaction());

        PaymentIntent paymentIntent = paymentService.createPaymentIntent(auction, user, amount, type, PaymentIntentCreateParams.CaptureMethod.AUTOMATIC);

        assertNotNull(paymentIntent);
        assertEquals("usd", paymentIntent.getCurrency());
        verify(transactionService).saveTransaction(any(Transaction.class)); // Verify the transaction is saved
    }

    // 6. Test capturePaymentIntent
    @Test
    void testCapturePaymentIntent() throws StripeException {
        Transaction transaction = new Transaction();
        transaction.setPaymentIntentId("pi_12345");

        PaymentIntent capturedIntent = paymentService.capturePaymentIntent(transaction);

        assertNotNull(capturedIntent);
        assertEquals("succeeded", capturedIntent.getStatus());
        verify(transactionService).saveTransaction(any(Transaction.class)); // Verify the transaction is saved
    }

    // 7. Test cancelPaymentIntent
    @Test
    void testCancelPaymentIntent() throws StripeException {
        Transaction transaction = new Transaction();
        transaction.setPaymentIntentId("pi_12345");

        PaymentIntent canceledIntent = paymentService.cancelPaymentIntent(transaction);

        assertNotNull(canceledIntent);
        assertEquals("canceled", canceledIntent.getStatus());
        verify(transactionService).saveTransaction(any(Transaction.class)); // Verify the transaction is saved
    }

    // 8. Test addCardWithoutDuplicate
    @Test
    void testAddCardWithoutDuplicate() throws StripeException {
        User user = new User();
        user.setPaymentAccount(new PaymentAccount());
        user.getPaymentAccount().setCustomerId("cus_12345");

        when(userService.save(any(User.class))).thenReturn(user);

        String result = paymentService.addCardWithoutDuplicate("tok_12345", user);

        assertEquals("Payment method added successfully.", result);
        verify(userService).save(user); // Ensure user is saved with the new payment method
    }

    // 9. Test transferFundsFromConnectedAccount
    @Test
    void testTransferFundsFromConnectedAccount() throws StripeException {
        Transfer transfer = paymentService.transferFundsFromConnectedAccount("acct_12345", 10000L, "Transfer to platform");

        assertNotNull(transfer);
        assertEquals("usd", transfer.getCurrency());
    }

    // 10. Test transferFundsToConnectedAccount
    @Test
    void testTransferFundsToConnectedAccount() throws StripeException {
        Transfer transfer = paymentService.transferFundsToConnectedAccount("acct_12345", 10000L, "Transfer to user");

        assertNotNull(transfer);
        assertEquals("usd", transfer.getCurrency());
    }
}

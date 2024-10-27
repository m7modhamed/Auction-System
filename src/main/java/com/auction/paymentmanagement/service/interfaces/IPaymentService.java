package com.auction.paymentmanagement.service.interfaces;

import com.auction.auctionmanagement.model.Auction;
import com.auction.paymentmanagement.enums.TransactionType;
import com.auction.paymentmanagement.model.Transaction;
import com.auction.usersmanagement.model.SysAccount;
import com.auction.usersmanagement.model.User;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.PaymentIntentCreateParams;

import java.util.List;


public interface IPaymentService {


     String createExpressAccountForUser(User user) ;

     Transfer transferFundsToConnectedAccount(String connectedAccountId, long amount , String description) throws StripeException;

     Transfer transferFundsFromConnectedAccount(String connectedAccountId, long amount , String description) throws StripeException;

     List<Transfer> retrieveIncomingTransfers(String connectedAccountId) throws StripeException;

     List<Transfer> retrieveOutgoingTransfers(String connectedAccountId) throws StripeException;

     ExternalAccount createBankAccount(String connectedAccountId) throws Exception;
     String getBankAccountsByConnectedAccountId(String connectedAccountId) throws StripeException;


     String addCardWithoutDuplicate(String token, User user) throws StripeException;
     List<PaymentMethod> getCustomerPaymentMethods(String customerId) throws StripeException;

     PaymentIntent createPaymentIntent(Auction auction , User user, Long amount, TransactionType type , PaymentIntentCreateParams.CaptureMethod captureMethod ) throws StripeException;
     ChargeCollection listChargesForPaymentMethod(String paymentMethodId) throws StripeException;
     public Customer createCustomer(SysAccount user) throws StripeException;


     PaymentIntent cancelPaymentIntent(Transaction transaction) throws StripeException;
     PaymentIntent capturePaymentIntent(Transaction transaction) throws StripeException;


}

package com.auction.Service.Interfaces;

import com.auction.Entity.Account;
import com.stripe.exception.StripeException;
import com.stripe.model.*;

import java.util.List;

public interface IPaymentService {


     Customer createCustomer(Account user) throws StripeException;

     String addCardWithoutDuplicate(String token, String customerId) throws StripeException;

     List<PaymentMethod> getCustomerPaymentMethods(String customerId) throws StripeException;

     PaymentIntent createPaymentIntent(String customerId, String paymentMethodId, Long amount, String currency, String description) throws StripeException;

     Refund createRefund(String chargeId, Long amount) throws StripeException;

     ChargeCollection listChargesForPaymentMethod(String paymentMethodId) throws Exception;

    }

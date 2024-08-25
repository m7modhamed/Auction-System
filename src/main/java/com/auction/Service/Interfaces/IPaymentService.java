package com.auction.Service.Interfaces;

import com.auction.security.entites.Account;
import com.stripe.exception.StripeException;
import com.stripe.model.*;

import java.util.List;

public interface IPaymentService {


    public Customer createCustomer(Account user) throws StripeException;


    public Customer getCustomer(String id) throws StripeException;

    public String addCardWithoutDuplicate(String token, String customerId) throws StripeException;

    public List<PaymentMethod> getCustomerPaymentMethods(String customerId) throws StripeException;


    public PaymentIntent createPaymentIntent(String customerId, String paymentMethodId, Long amount, String currency, String description) throws StripeException;

    public Refund createRefund(String chargeId, Long amount) throws StripeException;

    }

package com.auction.Service.Interfaces;

import com.auction.security.entites.Account;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;

import java.util.List;

public interface IPaymentService {


    public Customer createCustomer(Account user) throws StripeException;


    public Customer getCustomer(String id) throws StripeException;

    public String addCardWithoutDuplicate(String token, String customerId) throws StripeException;

    public List<PaymentMethod> getCustomerPaymentMethods(String customerId) throws StripeException;
}

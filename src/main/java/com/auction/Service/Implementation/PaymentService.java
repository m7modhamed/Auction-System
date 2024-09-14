package com.auction.Service.Implementation;


import com.auction.Service.Interfaces.IPaymentService;
import com.auction.Entity.Account;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {

    @Value("${stripe.key}")
    private String stripeKey;
    @Override
    public Customer createCustomer(Account user) throws StripeException {
        Stripe.apiKey=stripeKey;

        CustomerCreateParams params = CustomerCreateParams.builder()
                .setName(user.getFirstName() + " " +user.getLastName())
                .setEmail(user.getEmail())
                .build();

        return Customer.create(params);
    }




    public Customer getCustomer(String id) throws StripeException {
        Stripe.apiKey=stripeKey;

        return Customer.retrieve(id);


    }



    public String addCardWithoutDuplicate(String token, String customerId) throws StripeException {
        Stripe.apiKey=stripeKey;

        // Retrieve existing payment methods
        PaymentMethodListParams listParams = PaymentMethodListParams.builder()
                .setCustomer(customerId)
                .setType(PaymentMethodListParams.Type.CARD)
                .build();


        PaymentMethodCollection paymentMethods = PaymentMethod.list(listParams);

        // Get card details from token
        PaymentMethodCreateParams createParams = PaymentMethodCreateParams.builder()
                .setType(PaymentMethodCreateParams.Type.CARD)
                .setCard(PaymentMethodCreateParams.Token.builder()
                        .setToken(token)
                        .build())
                .build();
        PaymentMethod newPaymentMethod = PaymentMethod.create(createParams);

        // Check for duplicates
        for (PaymentMethod paymentMethod : paymentMethods.getData()) {
            if (paymentMethod.getCard().getLast4().equals(newPaymentMethod.getCard().getLast4())
                    && paymentMethod.getCard().getExpMonth().equals(newPaymentMethod.getCard().getExpMonth())
                    && paymentMethod.getCard().getExpYear().equals(newPaymentMethod.getCard().getExpYear())) {
                return "Card already exists";
            }
        }

        // Attach the new payment method to the customer
        PaymentMethodAttachParams attachParams = PaymentMethodAttachParams.builder()
                .setCustomer(customerId)
                .build();
        newPaymentMethod.attach(attachParams);

        return "Payment method added successfully.";
    }


    public List<PaymentMethod> getCustomerPaymentMethods(String customerId) throws StripeException {
        PaymentMethodListParams params = PaymentMethodListParams.builder()
                .setCustomer(customerId)
                .setType(PaymentMethodListParams.Type.CARD)
                .build();

        PaymentMethodCollection paymentMethods = PaymentMethod.list(params);
        return paymentMethods.getData();
    }



    public PaymentIntent createPaymentIntent(String customerId, String paymentMethodId, Long amount, String currency, String description) throws StripeException {
        Stripe.apiKey=stripeKey;

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount) // Amount in the smallest currency unit (e.g., cents for USD)
                .setCurrency(currency) // Currency code (e.g., "usd")
                .setCustomer(customerId) // The customer ID in Stripe
                .setPaymentMethod(paymentMethodId) // The payment method ID
                .setDescription(description) // Description of the payment
                .setConfirm(true) // Automatically confirm the payment intent
                .setOffSession(true) // Optional: Required if you're charging the customer without their immediate interaction
                .build();

        return PaymentIntent.create(params); // This will create and confirm the payment intent
    }


    public Refund createRefund(String chargeId, Long amount) throws StripeException {
        Stripe.apiKey = stripeKey;

        RefundCreateParams params = RefundCreateParams.builder()
                .setCharge(chargeId) // The ID of the charge to be refunded
                .setAmount(amount) // Amount to be refunded (optional, if not set, full amount is refunded)
                .build();

        return Refund.create(params); // Creates and returns the refund
    }





    public ChargeCollection listChargesForPaymentMethod(String paymentMethodId) throws StripeException {
        Stripe.apiKey = stripeKey;

        // Retrieve the PaymentMethod
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);

        // Check if the PaymentMethod is associated with a Customer
        if (paymentMethod.getCustomer() != null) {
            String customerId = paymentMethod.getCustomer();

            // List all charges associated with the customer
            ChargeListParams params = ChargeListParams.builder()
                    .setCustomer(customerId)
                    .setLimit(100L) // Adjust the limit as needed
                    .build();

            return Charge.list(params);
        } else {
            throw new IllegalArgumentException("No customer associated with this PaymentMethod.");
        }
    }
}




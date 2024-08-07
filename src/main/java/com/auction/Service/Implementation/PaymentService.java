package com.auction.Service.Implementation;


import com.auction.Service.Interfaces.IPaymentService;
import com.auction.security.entites.Account;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.PaymentMethodCreateParams;
import com.stripe.param.PaymentMethodListParams;
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


    public PaymentSource addCard(String customerId) throws StripeException {
        Stripe.apiKey=stripeKey;

        // Retrieve the customer object from Stripe with sources expanded
        Customer customer = Customer.retrieve(customerId);

        // Card parameters
        Map<String, Object> cardParams = new HashMap<>();
        cardParams.put("number", "4242424242424242");
        cardParams.put("exp_month", 12);
        cardParams.put("exp_year", 2029);
        cardParams.put("cvc", "222");

        // Create token parameters
        Map<String, Object> tokenParams = new HashMap<>();
        tokenParams.put("card", cardParams);

        // Create token
        Token token = Token.create(tokenParams);

        // Create source parameters
        Map<String, Object> sourceParams = new HashMap<>();
        sourceParams.put("source", token.getId());

        // Add card to customer
        return customer.getSources().create(sourceParams);
    }


    public String addCardWithoutDuplicate(String token, String customerId) throws StripeException {
        Stripe.apiKey="sk_test_51PPBVn2KC2UBlHde0woMNEtoYoWO8vtKvR2BjujHTQkbb2jnGYWd1kfQ0z7cmVRuH0mGxozZNVt9goIUK6KMJh2d00xlPDDx6D";

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


}

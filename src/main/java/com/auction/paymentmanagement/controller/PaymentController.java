package com.auction.paymentmanagement.controller;

import com.auction.paymentmanagement.service.interfaces.IPaymentService;
import com.auction.usersmanagement.model.User;
import com.auction.common.utility.AccountUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stripe") // Base URL for this controller
public class PaymentController {

    private final IPaymentService paymentService;

    @PostMapping("/create-account")
    public String createAccount() {

        User user = (User) AccountUtil.getCurrentAccount();
        return paymentService.createExpressAccountForUser(user);
    }

    @PostMapping("/add-card")
    public ResponseEntity<String> addCard(@RequestParam String token) {
        User user=(User)AccountUtil.getCurrentAccount();

        try {
            String result = paymentService.addCardWithoutDuplicate(token, user);
            return ResponseEntity.ok(result);
        } catch (StripeException e) {
            return new ResponseEntity<>("Failed to add card: " + e.getUserMessage(), HttpStatus.valueOf(e.getStatusCode()));
        }
    }

    @GetMapping("/customer-cards")
    public ResponseEntity<?> getCustomerPaymentMethods() {
        try {
            String customerId=((User)AccountUtil.getCurrentAccount()).getPaymentAccount().getCustomerId();

            List<PaymentMethod> paymentMethods = paymentService.getCustomerPaymentMethods(customerId);
            return ResponseEntity.ok(paymentMethods);
        } catch (StripeException e) {
            return new ResponseEntity<>("Failed to retrieve payment methods: " + e.getMessage(), HttpStatus.valueOf(e.getStatusCode()));
        } catch (Exception e) {
            return new ResponseEntity<>("Internal Server Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /*
    @PostMapping("/charge")
    public ResponseEntity<ChargeResponse> createCharge(@RequestBody ChargeRequest chargeRequest) throws StripeException {
        User user=(User)AccountUtil.getCurrentAccount();

        PaymentIntent paymentIntent=paymentService.createPaymentIntent(
                user.getPaymentAccount().getCustomerId(),
                chargeRequest.getPaymentMethodId(),
                chargeRequest.getAmount(),
                chargeRequest.getDescription()
        );

        // Map the fields you need to your DTO
        ChargeResponse response = new ChargeResponse();
        response.setId(paymentIntent.getId());
        response.setAmount(paymentIntent.getAmount());
        response.setCurrency(paymentIntent.getCurrency());
        response.setStatus(paymentIntent.getStatus());
        response.setDescription(paymentIntent.getDescription());


        return ResponseEntity.ok().body(response);
    }*/




    @GetMapping("listCharges/{methodId}")
    public String getAllCharges(@PathVariable String methodId){
        try {
            return paymentService.listChargesForPaymentMethod(methodId).toJson();
        } catch (StripeException e) {

            throw new RuntimeException("Stripe API error: " + e.getMessage(), e);
        } catch (Exception e) {

            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }


    }

    @GetMapping("/transferTo")
    public ResponseEntity<String> transferFundsToConnectedAccount(){
        User user = (User) AccountUtil.getCurrentAccount();

        try {
            paymentService.transferFundsToConnectedAccount(user.getPaymentAccount().getStripeAccountId() , 9000, "gift to you");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok("success");
    }

    @GetMapping("/transferFrom")
    public ResponseEntity<String> transferFundsFromConnectedAccount(){
        User user = (User) AccountUtil.getCurrentAccount();

        try {
            paymentService.transferFundsFromConnectedAccount(user.getPaymentAccount().getStripeAccountId() , 1000 , "charge auction fee.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok("success");
    }


    // Endpoint to retrieve incoming transfers for a connected account
    @GetMapping("/incoming")
    public List<Transfer> getIncomingTransfers() {
        User user = (User) AccountUtil.getCurrentAccount();

        try {
            return paymentService.retrieveIncomingTransfers(user.getPaymentAccount().getStripeAccountId());
        } catch (StripeException e) {
            // Handle StripeException (e.g., log error, return appropriate response)
            throw new RuntimeException("Error retrieving incoming transfers: " + e.getMessage(), e);
        }
    }

    // Endpoint to retrieve outgoing transfers for a connected account
    @GetMapping("/outgoing")
    public List<Transfer> getOutgoingTransfers() {
        User user = (User) AccountUtil.getCurrentAccount();

        try {
            return paymentService.retrieveOutgoingTransfers(user.getPaymentAccount().getStripeAccountId());
        } catch (StripeException e) {
            // Handle StripeException (e.g., log error, return appropriate response)
            throw new RuntimeException("Error retrieving outgoing transfers: " + e.getMessage(), e);
        }
    }

    @GetMapping("createExternalAccount")
    public ResponseEntity<String> createExternalAccount() {

        User user = (User) AccountUtil.getCurrentAccount();
        try {
            paymentService.createBankAccount(user.getPaymentAccount().getStripeAccountId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok("success");
    }



}

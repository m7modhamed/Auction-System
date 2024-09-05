package com.auction.Controller;

import com.auction.Dtos.ChargeRequest;
import com.auction.Dtos.ChargeResponse;
import com.auction.Dtos.RefundRequest;
import com.auction.Service.Interfaces.IPaymentService;
import com.auction.security.entites.User;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stripe") // Base URL for this controller
public class PaymentController {

    private final IPaymentService paymentService;


    @PostMapping("/add-card")
    public ResponseEntity<String> addCard(@RequestParam String token) {
        User user=getCurrentUserId();

        try {
            String result = paymentService.addCardWithoutDuplicate(token, user.getPaymentAccount().getCustomerId());
            return ResponseEntity.ok(result);
        } catch (StripeException e) {
            return new ResponseEntity<>("Failed to add card: " + e.getUserMessage(), HttpStatus.valueOf(e.getStatusCode()));
        }
    }

    @GetMapping("/customer-cards")
    public ResponseEntity<?> getCustomerPaymentMethods() {
        try {
            String customerId=getCurrentUserId().getPaymentAccount().getCustomerId();

            List<PaymentMethod> paymentMethods = paymentService.getCustomerPaymentMethods(customerId);
            return ResponseEntity.ok(paymentMethods);
        } catch (StripeException e) {
            return new ResponseEntity<>("Failed to retrieve payment methods: " + e.getMessage(), HttpStatus.valueOf(e.getStatusCode()));
        } catch (Exception e) {
            return new ResponseEntity<>("Internal Server Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/charge")
    public ResponseEntity<ChargeResponse> createCharge(@RequestBody ChargeRequest chargeRequest) throws StripeException {
        PaymentIntent paymentIntent=paymentService.createPaymentIntent(
                getCurrentUserId().getPaymentAccount().getCustomerId(),
                chargeRequest.getPaymentMethodId(),
                chargeRequest.getAmount(),
                chargeRequest.getCurrency(),
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
    }

    @PostMapping("/refund")
    public ResponseEntity<Refund> createRefund(@RequestBody RefundRequest refundRequest) {
        try {
            Refund refund = paymentService.createRefund(
                    refundRequest.getChargeId(),
                    refundRequest.getAmount()
            );
            return ResponseEntity.ok(refund);
        } catch (StripeException e) {
            return ResponseEntity.status(500).body(null); // Adjust error handling as needed
        }
    }

    @GetMapping("listCharges/{methodId}")
    public ChargeCollection getAllCharges(@PathVariable String methodId){
        try {
            return paymentService.listChargesForPaymentMethod(methodId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    private User getCurrentUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((User) authentication.getPrincipal());
    }





}

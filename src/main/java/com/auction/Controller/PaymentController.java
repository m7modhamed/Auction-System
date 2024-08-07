package com.auction.Controller;

import com.auction.Service.Interfaces.IPaymentService;
import com.auction.security.entites.Account;
import com.auction.security.entites.User;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final IPaymentService paymentService;


    @PostMapping("/api/stripe/add-card")
    public ResponseEntity<String> addCard(@RequestParam String token) {
        User user=getCurrentUserId();

        try {
            String result = paymentService.addCardWithoutDuplicate(token, user.getPaymentAccount().getCustomerId());
            return ResponseEntity.ok(result);
        } catch (StripeException e) {
            return new ResponseEntity<>("Failed to add card: " + e.getUserMessage(), HttpStatus.valueOf(e.getStatusCode()));
        }
    }

    @GetMapping("/api/stripe/customer-cards")
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


    private User getCurrentUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((User) authentication.getPrincipal());
    }

}

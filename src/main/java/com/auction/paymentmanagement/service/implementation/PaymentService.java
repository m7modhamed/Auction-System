package com.auction.paymentmanagement.service.implementation;


import com.auction.auctionmanagement.model.Auction;
import com.auction.paymentmanagement.enums.TransactionType;
import com.auction.paymentmanagement.model.PaymentAccount;
import com.auction.paymentmanagement.model.Transaction;
import com.auction.paymentmanagement.service.interfaces.IPaymentService;
import com.auction.usersmanagement.model.SysAccount;
import com.auction.usersmanagement.model.User;
import com.auction.common.exceptions.AppException;
import com.auction.usersmanagement.service.implementation.UserService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.net.RequestOptions;
import com.stripe.param.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.stripe.param.AccountCreateParams;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService implements IPaymentService {

    private final UserService userService;
    private final TransactionService transactionService;

    @Value("${stripe.key}")
    private String stripeKey;


    @PostConstruct
    private void init() {
        Stripe.apiKey=stripeKey;
    }


    public String createExpressAccountForUser(User user) {
        AccountLink accountLink;
        try {
            // Step 1: Create a new connected account
            AccountCreateParams accountParams = AccountCreateParams.builder()
                    .setType(AccountCreateParams.Type.EXPRESS) // or CUSTOM based on your needs
                    .setCountry("US") // or the user's country
                    .setEmail(user.getEmail())
                    .setBusinessType(AccountCreateParams.BusinessType.INDIVIDUAL) // or COMPANY
                    .setBusinessProfile(AccountCreateParams.BusinessProfile.builder()
                            .setProductDescription("freelance service").build())
                    .build();

            Account account = Account.create(accountParams);




            if(user.getPaymentAccount() == null){
                PaymentAccount paymentAccount = new PaymentAccount();
                paymentAccount.setOwner(user);
                user.setPaymentAccount(paymentAccount);
            }
            user.getPaymentAccount().setStripeAccountId(account.getId());
            userService.save(user);

            // Step 2: Create an account link for onboarding
            AccountLinkCreateParams accountLinkParams = AccountLinkCreateParams.builder()
                    .setAccount(account.getId())
                    .setRefreshUrl("https://yourdomain.com/reauth") // URL for refreshing the account link
                    .setReturnUrl("https://yourdomain.com/success") // URL to return after onboarding
                    .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING) // Type of link
                    .build();

            accountLink = AccountLink.create(accountLinkParams);

        } catch (StripeException e) {
            throw new AppException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        // Step 3: Return the account link URL for redirection
        return accountLink.getUrl();
    }

    @Override
    public Customer createCustomer(SysAccount user) throws StripeException {

        CustomerCreateParams params = CustomerCreateParams.builder()
                .setName(user.getFirstName() + " " +user.getLastName())
                .setEmail(user.getEmail())
                .build();

        return Customer.create(params);
    }



    public List<PaymentMethod> getCustomerPaymentMethods(String customerId) throws StripeException {
        PaymentMethodListParams params = PaymentMethodListParams.builder()
                .setCustomer(customerId)
                .setType(PaymentMethodListParams.Type.CARD)
                .build();

        PaymentMethodCollection paymentMethods = PaymentMethod.list(params);
        return paymentMethods.getData();
    }

    public PaymentIntent createPaymentIntent(Auction auction , User user, Long amount, TransactionType type ,  PaymentIntentCreateParams.CaptureMethod captureMethod ) throws StripeException {
        PaymentAccount paymentAccount = user.getPaymentAccount();


        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount) // Amount in the smallest currency unit (e.g., cents for USD)
                .setCurrency("usd") // Currency code (e.g., "usd")
                .setCustomer(paymentAccount.getCustomerId()) // The customer ID in Stripe
                .setPaymentMethod(paymentAccount.getPaymentMethod().getStripePaymentMethodId()) // The payment method ID
                .setDescription(type.name()) // Description of the payment
                .setCaptureMethod(captureMethod)
                .setConfirm(true) // Automatically confirm the payment intent
                .setOffSession(true) // Optional: Required if you're charging the customer without their immediate interaction
                .build();

        PaymentIntent paymentIntent =  PaymentIntent.create(params); // This will create and confirm the payment intent

        Transaction transaction= new Transaction();
        transaction.setPaymentIntentId(paymentIntent.getId());
        transaction.setStatus(paymentIntent.getStatus());
        transaction.setAmountInCent(paymentIntent.getAmount());
        transaction.setCurrency(paymentIntent.getCurrency());
        transaction.setAuction(auction);
        transaction.setPaymentMethod(paymentAccount.getPaymentMethod());
        transaction.setType(type);
        transactionService.saveTransaction(transaction);

        return paymentIntent;
    }

    public PaymentIntent capturePaymentIntent(Transaction transaction) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(transaction.getPaymentIntentId());
        PaymentIntent newPaymentIntent =  paymentIntent.capture();
        transaction.setStatus(newPaymentIntent.getStatus());

        transactionService.saveTransaction(transaction);
        return newPaymentIntent;
    }

    public PaymentIntent cancelPaymentIntent(Transaction transaction) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(transaction.getPaymentIntentId());
        PaymentIntent newPaymentIntent =  paymentIntent.cancel();
        transaction.setStatus(newPaymentIntent.getStatus());

        transactionService.saveTransaction(transaction);
        return newPaymentIntent;
    }


    public String addCardWithoutDuplicate(String token, User user) throws StripeException {
        String customerId = user.getPaymentAccount().getCustomerId();
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


        savePaymentMethod(newPaymentMethod , user);

        return "Payment method added successfully.";
    }

    private void savePaymentMethod(PaymentMethod newPaymentMethod ,User user)  {
        com.auction.paymentmanagement.model.PaymentMethod myPaymentMethod = new com.auction.paymentmanagement.model.PaymentMethod();
        myPaymentMethod.setStripePaymentMethodId(newPaymentMethod.getId());
        myPaymentMethod.setType(newPaymentMethod.getType());
        myPaymentMethod.setExpMonth(newPaymentMethod.getCard().getExpMonth().toString());
        myPaymentMethod.setExpYear(newPaymentMethod.getCard().getExpYear().toString());
        myPaymentMethod.setLast4(newPaymentMethod.getCard().getLast4());
        user.getPaymentAccount().setPaymentMethod(myPaymentMethod);

        userService.save(user);
    }

   /* public PaymentIntent createPaymentIntent(String customerId, String paymentMethodId, long amount) throws StripeException {
        // Create a PaymentIntent
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount) // Amount in cents
                .setCurrency("usd") // Currency (e.g., "usd")
                .setCustomer(customerId) // Specify the customer
                .setPaymentMethod(paymentMethodId) // Set the payment method directly
                .setConfirm(true) // Automatically confirm the PaymentIntent
                .putExtraParam("automatic_payment_methods[enabled]", true) // Enable automatic payment methods
                .putExtraParam("automatic_payment_methods[allow_redirects]", "never") // Avoid redirects
                .build();

        return PaymentIntent.create(params);
    }*/



    public ChargeCollection listChargesForPaymentMethod(String paymentMethodId) throws StripeException {

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

    public Transfer transferFundsFromConnectedAccount(String connectedAccountId, long amount , String description) throws StripeException {

        // Step 1: Retrieve the balance of the connected account
        Balance balance = Balance.retrieve(RequestOptions.builder().setStripeAccount(connectedAccountId).build());

        // Get available balance in the primary currency (e.g., USD)
        List<Balance.Available> availableBalances = balance.getAvailable();
        long availableBalanceInUsd = 0;

        // Find the available balance for the currency you want to transfer
        for (Balance.Available money : availableBalances) {
            if (money.getCurrency().equals("usd")) { // Checking for USD balance
                availableBalanceInUsd = money.getAmount();
                break;
            }
        }

        // Step 2: Check if the balance is sufficient for the transfer
        if (availableBalanceInUsd < amount) {
            throw new AppException("Insufficient balance for the transfer." , HttpStatus.BAD_REQUEST);
        }

        // Step 3: If sufficient balance, proceed with the transfer
        TransferCreateParams transferParams = TransferCreateParams.builder()
                .setAmount(amount) // Amount to transfer in cents
                .setCurrency("usd") // Currency, e.g., "usd"
                .setDescription(description)
                .setDestination("acct_1PshbnE51UEbJoiH") // Your platform account ID
                .build();

        // Create the transfer using the connected account's ID
        return Transfer.create(transferParams, RequestOptions.builder().setStripeAccount(connectedAccountId).build());
    }



    public Transfer transferFundsToConnectedAccount(String connectedAccountId, long amount , String description) throws StripeException {

        // Step 1: Create a Transfer
        TransferCreateParams transferParams = TransferCreateParams.builder()
                .setAmount(amount) // Amount to transfer (in cents)
                .setCurrency("usd") // Transfer currency (USD in this case)
                .setDescription(description)
                .setDestination(connectedAccountId) // Connected SysAccount ID
                .build();

        // Step 2: Perform the transfer and return the result
        return Transfer.create(transferParams);
    }



    public List<Transfer> retrieveIncomingTransfers(String connectedAccountId) throws StripeException {
        // Create request options with the connected account's Stripe ID
        RequestOptions requestOptions = RequestOptions.builder()
                .setStripeAccount(connectedAccountId)  // Specify the connected account ID
                .build();

        // Set the parameters to filter transfers (for incoming transfers to this account)
        TransferListParams params = TransferListParams.builder()
                .setDestination(connectedAccountId)  // Destination = Connected account ID
                .build();

        return Transfer.list(params, requestOptions).getData();
    }


    public List<Transfer> retrieveOutgoingTransfers(String connectedAccountId) throws StripeException {
        // Create request options with the connected account's Stripe ID
        RequestOptions requestOptions = RequestOptions.builder()
                .setStripeAccount(connectedAccountId)  // Specify the connected account ID
                .build();

        // Set parameters to list all transfers (outgoing from the connected account)
        TransferListParams params = TransferListParams.builder().build();


        return  Transfer.list(params, requestOptions).getData();
    }


    public PaymentIntent createPaymentIntent(String connectedAccountId, long amount, String paymentMethodId) throws StripeException {
        // Create PaymentIntent parameters
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount) // Amount in cents
                .setCurrency("usd") // Currency (e.g., "usd")
                .setPaymentMethod(paymentMethodId) // Payment method ID (obtained from the frontend)
                .setConfirm(true) // Automatically confirm the PaymentIntent
                .build();

        // Create request options with the connected account ID
        RequestOptions requestOptions = RequestOptions.builder()
                .setStripeAccount(connectedAccountId) // Set the connected account ID
                .build();

        // Create and return the PaymentIntent
        return PaymentIntent.create(params, requestOptions);
    }




    public void retrieveAndVerifyBankAccount(String connectedAccountId) throws Exception {

        // Retrieve the connected account
        com.stripe.model.Account account = com.stripe.model.Account.retrieve(connectedAccountId);

        // Retrieve bank accounts for the connected account
        ExternalAccountCollection bankAccounts = account.getExternalAccounts();

        // Check if any bank accounts are associated
        if (bankAccounts.getData().isEmpty()) {
            System.out.println("No bank accounts found for this connected account.");
            return;
        }

        // Assuming you want to verify the first bank account found
        ExternalAccount bankAccount = bankAccounts.getData().get(1);

        // Call your verification method
    }


    public String getBankAccountsByConnectedAccountId(String connectedAccountId) throws StripeException {
        // Retrieve the connected account
        Account account = Account.retrieve(connectedAccountId);

        // List external accounts (bank accounts) associated with the connected account
        ExternalAccountCollection bankAccounts = account.getExternalAccounts();

        return bankAccounts.getData().get(0).getId(); // Return the list of bank accounts
    }






    public ExternalAccount createBankAccount(String connectedAccountId) throws Exception {

        // Retrieve the connected account
        com.stripe.model.Account account = com.stripe.model.Account.retrieve(connectedAccountId);

        // Create a map for the bank account details
        Map<String, Object> bankAccountParams = new HashMap<>();
        bankAccountParams.put("object", "bank_account");
        bankAccountParams.put("account_number", "000123456789"); // Test account number
        bankAccountParams.put("routing_number", "110000000");    // Test routing number

        // Create a new bank account for the connected account
       return account.getExternalAccounts().create(bankAccountParams);

    }




}




package com.auction.security.services;

import com.auction.Entity.PaymentAccount;
import com.auction.Repository.PaymentAccountRepository;
import com.auction.Service.Interfaces.IPaymentService;
import com.auction.Service.Interfaces.IimageService;
import com.auction.security.dtos.UserAuthDto;
import com.auction.security.entites.Account;
import com.auction.security.entites.User;
import com.auction.security.password.IpasswordResetTokenService;
import com.auction.security.dtos.CredentialsDto;
import com.auction.security.dtos.SignUpDto;
import com.auction.security.entites.Role;
import com.auction.security.event.RegistrationCompleteEvent;
import com.auction.security.event.listener.RegistrationCompleteEventListener;
import com.auction.exceptions.AppException;
import com.auction.security.mappers.UserMapper;
import com.auction.security.repositories.AccountRepository;
import com.auction.security.repositories.RoleRepository;
import com.auction.security.repositories.UserRepository;
import com.auction.security.utility.UrlUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final IPaymentService paymentService;
    private final PasswordEncoder passwordEncoder;
    private final IpasswordResetTokenService passwordResetTokenService;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher publisher;
    private final RegistrationCompleteEventListener eventListener;
    private final PaymentAccountRepository paymentAccountRepository;
    private final AuthenticationManager authenticationManager;
    private final IimageService imageService;



    public UserAuthDto login(CredentialsDto credentialsDto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        credentialsDto.login(),
                        credentialsDto.password()
                )
        );
        var user = accountRepository.findByEmail(credentialsDto.login())
                .orElseThrow();
        return userMapper.UserAuthDto(user);

    }

    @Transactional
    public UserAuthDto register(SignUpDto userDto, HttpServletRequest request) {
        Optional<Account> optionalAccount = accountRepository.findByEmail(userDto.email());

        if (optionalAccount.isPresent()) {
            throw new AppException("Login already exists", HttpStatus.BAD_REQUEST);
        }


        User user = userMapper.signUpToUser(userDto);
        // these lines for testing
        user.setEmail(userDto.email());
        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        user.setImage(userDto.image());
        /******/
        user.setMyPassword(passwordEncoder.encode(userDto.password()));
        user.setIsBlocked(false);
        user.setIsActive(false);
        user.setRoles(new HashSet<>());
        Optional<Role> role=roleRepository.findByName("ROLE_USER");
        if(role.isPresent()) {
            user.getRoles().add(role.get());
        }else{
            throw new AppException("the role not found",HttpStatus.NOT_FOUND);
        }

        //add new customer in stripe payment getaway
        try {
            //create a customer in stripe
            Customer customer=paymentService.createCustomer(user);

            //create payment account & add the customer id that generated from stripe to this payment account
            PaymentAccount paymentAccount=new PaymentAccount();
            paymentAccount.setCustomerId(customer.getId());
            paymentAccount.setOwner(user);

            //  save payment account and associate with the user
            user.setPaymentAccount(paymentAccount);

        } catch (StripeException e) {
            throw new AppException("Failed to create stripe customer",HttpStatus.valueOf(e.getStatusCode()));
        }
        imageService.save(userDto.image());
        User savedAccount = userRepository.save(user);
        publisher.publishEvent(new RegistrationCompleteEvent(savedAccount, UrlUtil.getApplicationUrl(request)));
        return userMapper.UserAuthDto(savedAccount);
    }

    @Transactional(readOnly = true)
    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
    }


    public void resetPasswordRequest(Account account, HttpServletRequest request) {
        String passwordResetToken = UUID.randomUUID().toString();
        passwordResetTokenService.createPasswordResetTokenForUser(account, passwordResetToken);

        // Send password reset verification email to the account
        String url = UrlUtil.getApplicationUrl(request) + "/reset-password?token=" + passwordResetToken;
        try {
            eventListener.sendPasswordResetVerificationEmail(url, account);
        } catch (UnsupportedEncodingException | MessagingException e) {
            throw new AppException("Error sending password reset email", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public String resetPassword(String token, String password) {
        String tokenVerificationResult = passwordResetTokenService.validatePasswordResetToken(token);

        if (!tokenVerificationResult.equalsIgnoreCase("valid")) {
            return "Invalid token";
        }

        Optional<Account> theAccount = passwordResetTokenService.findUserByPasswordResetToken(token);
        if (theAccount.isPresent()) {
            passwordResetTokenService.resetPassword(theAccount.get(), password);
            return "Password reset successfully";
        }

        return "User not found";
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }



}

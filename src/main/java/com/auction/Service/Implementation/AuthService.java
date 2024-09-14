package com.auction.Service.Implementation;

import com.auction.Dtos.SignUpRequestDto;
import com.auction.Entity.PaymentAccount;
import com.auction.Mappers.IUserMapper;
import com.auction.Service.Interfaces.IPaymentService;
import com.auction.Service.Interfaces.IimageService;
import com.auction.Dtos.UserAuthDto;
import com.auction.Entity.Account;
import com.auction.Entity.User;
import com.auction.Service.Interfaces.IpasswordResetTokenService;
import com.auction.Dtos.LoginRequestDto;
import com.auction.Entity.Role;
import com.auction.event.RegistrationCompleteEvent;
import com.auction.event.listener.RegistrationCompleteEventListener;
import com.auction.exceptions.AppException;
import com.auction.Repository.AccountRepository;
import com.auction.Repository.RoleRepository;
import com.auction.Repository.UserRepository;
import com.auction.utility.UrlUtil;
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
public class AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final IPaymentService paymentService;
    private final PasswordEncoder passwordEncoder;
    private final IpasswordResetTokenService passwordResetTokenService;
    private final IUserMapper userMapper;
    private final ApplicationEventPublisher publisher;
    private final RegistrationCompleteEventListener eventListener;
    private final AuthenticationManager authenticationManager;
    private final IimageService imageService;



    public Account login(LoginRequestDto loginRequestDto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.login(),
                        loginRequestDto.password()
                )
        );
        return accountRepository.findByEmail(loginRequestDto.login())
                .orElseThrow();
    }

    @Transactional
    public UserAuthDto register(SignUpRequestDto signUpRequestDto, HttpServletRequest request) {
        Optional<Account> optionalAccount = accountRepository.findByEmail(signUpRequestDto.email());

        if (optionalAccount.isPresent()) {
            throw new AppException("Login already exists", HttpStatus.BAD_REQUEST);
        }

        User user = userMapper.signUpToUser(signUpRequestDto);
        // these lines for testing
        user.setEmail(signUpRequestDto.email());
        user.setFirstName(signUpRequestDto.firstName());
        user.setLastName(signUpRequestDto.lastName());
        user.setImage(signUpRequestDto.image());

        user.setMyPassword(passwordEncoder.encode(signUpRequestDto.password()));
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
        imageService.save(signUpRequestDto.image());
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

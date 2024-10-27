package com.auction.usersmanagement.service.implementation;

import com.auction.paymentmanagement.model.PaymentAccount;
import com.auction.paymentmanagement.service.interfaces.IPaymentService;
import com.auction.usersmanagement.dto.SignUpRequestDto;
import com.auction.usersmanagement.dto.UpdateAccountDto;
import com.auction.usersmanagement.mapper.IUserMapper;
import com.auction.usersmanagement.model.*;
import com.auction.usersmanagement.service.interfaces.IAccountService;
import com.auction.usersmanagement.dto.LoginRequestDto;
import com.auction.usersmanagement.event.RegistrationCompleteEvent;
import com.auction.usersmanagement.event.listener.RegistrationCompleteEventListener;
import com.auction.usersmanagement.repository.AccountRepository;
import com.auction.usersmanagement.repository.RoleRepository;
import com.auction.usersmanagement.repository.UserRepository;
import com.auction.common.exceptions.AppException;
import com.auction.common.utility.UrlUtil;
import com.auction.common.utility.AccountUtil;
import com.auction.usersmanagement.service.interfaces.ITokenService;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class AccountService implements IAccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final IUserMapper userMapper;
    private final ApplicationEventPublisher publisher;
    private final RegistrationCompleteEventListener eventListener;
    private final AuthenticationManager authenticationManager;
    private final ITokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final IPaymentService paymentService;


    public SysAccount login(LoginRequestDto loginRequestDto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getLogin(),
                        loginRequestDto.getPassword()
                )
        );
        return accountRepository.findByEmail(loginRequestDto.getLogin())
                .orElseThrow();
    }

    @Transactional
    public SysAccount register(SignUpRequestDto signUpRequestDto, HttpServletRequest request) {
        Optional<SysAccount> optionalAccount = accountRepository.findByEmail(signUpRequestDto.email());

        if (optionalAccount.isPresent()) {
            throw new AppException("Login already exists", HttpStatus.BAD_REQUEST);
        }

        User user = userMapper.signUpToUser(signUpRequestDto);

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

        User savedAccount = userRepository.save(user);
        publisher.publishEvent(new RegistrationCompleteEvent(savedAccount, UrlUtil.getClientUrl(request)));
        return savedAccount;
    }

    @Override
    public ResponseEntity<String> activateUser(String token) {
        Token tokenObj = tokenService.validateToken(token);

        SysAccount sysAccount = tokenObj.getSysAccount();
        if (sysAccount.getIsActive()) {
            throw new AppException( "The user sysAccount is already active." ,HttpStatus.CONFLICT);
        }

        sysAccount.setIsActive(true);
        accountRepository.save(sysAccount);
        tokenObj.setUsed(true);
        tokenService.saveToken(tokenObj);
        return ResponseEntity.ok("sysAccount verify successfully");
    }

    @Transactional(readOnly = true)
    public SysAccount findByEmail(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
    }


    public void resetPasswordRequest(SysAccount sysAccount, HttpServletRequest request) {
        String passwordResetToken = UUID.randomUUID().toString();
        tokenService.createPasswordResetTokenForUser(sysAccount, passwordResetToken);

        // Send password reset verification email to the sysAccount
        String url = UrlUtil.getClientUrl(request) + "/reset-password?token=" + passwordResetToken;
        try {
            eventListener.sendPasswordResetVerificationEmail(url, sysAccount);
        } catch (UnsupportedEncodingException | MessagingException e) {
            throw new AppException("Error sending password reset email", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String resetPassword(String token, String newPassword) {
        Token tokenObj=tokenService.validateToken(token);
        SysAccount sysAccount =tokenObj.getSysAccount();
        String encodedPassword = passwordEncoder.encode(newPassword);
        if (passwordEncoder.matches(newPassword, sysAccount.getMyPassword())) {
            throw new AppException("The new password must be different from the old password.", HttpStatus.BAD_REQUEST);
        }

        sysAccount.setMyPassword(encodedPassword);
        accountRepository.save(sysAccount);

        tokenObj.setUsed(true);
        tokenService.saveToken(tokenObj);

        return "Password reset successfully";
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }


    @Override
    public void updateAccount(UpdateAccountDto updateAccountDto) {

        long id = AccountUtil.getCurrentAccountId();

        Optional<SysAccount> account = getAccountById(id);

        if (account.isEmpty()) {
            throw new AppException("SysAccount not found.", HttpStatus.NOT_FOUND);
        } else if (account.get().getProfileImage() == null) {
            throw new AppException("No auctionImage associated with this account.", HttpStatus.NOT_FOUND);
        }

        account.get().setFirstName(updateAccountDto.getFirstName());
        account.get().setLastName(updateAccountDto.getLastName());
        // add auctionImage id to new auctionImage
        ProfileImage newImg=updateAccountDto.getProfileImage();
        newImg.setId(account.get().getProfileImage().getId());
        account.get().setProfileImage(newImg);

        accountRepository.save(account.get());

    }


    private Optional<SysAccount> getAccountById(Long accountId) {

        return accountRepository.findById(accountId);
    }



}

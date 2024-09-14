package com.auction.Controller;

import com.auction.Dtos.*;
import com.auction.Service.Interfaces.IAccountService;
import com.auction.security.config.UserAuthenticationProvider;
import com.auction.Entity.Account;
import com.auction.event.listener.RegistrationCompleteEventListener;
import com.auction.Mappers.IUserMapper;
import com.auction.Service.Interfaces.IpasswordResetTokenService;
import com.auction.Repository.AccountRepository;
import com.auction.Service.Implementation.AuthService;
import com.auction.Entity.VerificationToken;
import com.auction.Service.Implementation.VerificationTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);
    private final AccountRepository accountRepository;
    private final IAccountService accountService;
    private final AuthService authService;
    private final UserAuthenticationProvider userAuthenticationProvider;
    private final VerificationTokenService tokenService;
    private final IUserMapper userMapper;
    private final RegistrationCompleteEventListener eventListener;
    private final IpasswordResetTokenService passwordResetTokenService;



    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        Account account = authService.login(loginRequestDto);

        LoginResponseDto loginResponseDto =new LoginResponseDto();
        loginResponseDto.setStatus("success");
        loginResponseDto.setMessage("Login successful");

        String token=userAuthenticationProvider.createToken(account);

        loginResponseDto.setToken(token);
        return ResponseEntity.ok(loginResponseDto);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid SignUpRequestDto signUpRequestDto, HttpServletRequest request) {
        Account createdAccount = authService.register(signUpRequestDto,request);

        return ResponseEntity.created(URI.create("/users/" + createdAccount.getId())).body("User registered successfully. Please check your email to verify your account.");
    }

    @GetMapping("/verifyEmail")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        Optional<VerificationToken> theToken = tokenService.findByToken(token);

        if (theToken.isPresent() && theToken.get().getUser().getIsActive()) {
            return ResponseEntity.ok("the user already active");
        }

        return tokenService.validateToken(token);
    }



    @PostMapping("/forgot-password-request")
    public ResponseEntity<String> resetPasswordRequest(@RequestParam String email, HttpServletRequest request) {
        Account account = authService.findByEmail(email);
        authService.resetPasswordRequest(account, request);

        // Assuming the service method completes without exceptions, return success response.
        return ResponseEntity.ok("Password reset email sent successfully");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String password) {
        String resetResult = authService.resetPassword(token, password);
        HttpStatus status = resetResult.equals("Password reset successfully") ? HttpStatus.ACCEPTED : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(resetResult);
    }
    

    @PutMapping("/update-account")
    public ResponseEntity<Account> updateAccountInfo(@RequestBody UpdateAccountDto updateAccountDto){

        accountService.updateAccount(updateAccountDto);

        return ResponseEntity.noContent().build();

    }







}

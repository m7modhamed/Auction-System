package com.auction.Controller;

import com.auction.Dtos.*;
import com.auction.Service.Interfaces.IAccountService;
import com.auction.security.config.UserAuthenticationProvider;
import com.auction.Entity.Account;
import com.auction.Entity.VerificationToken;
import com.auction.Service.Implementation.VerificationTokenService;
import com.auction.validation.customAnnotations.ValidPassword;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@Validated
public class AccountController {

    private final IAccountService accountService;
    private final UserAuthenticationProvider userAuthenticationProvider;
    private final VerificationTokenService tokenService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        Account account = accountService.login(loginRequestDto);

        LoginResponseDto loginResponseDto =new LoginResponseDto();
        loginResponseDto.setStatus("success");
        loginResponseDto.setMessage("Login successful");

        String token=userAuthenticationProvider.createToken(account);

        loginResponseDto.setToken(token);
        return ResponseEntity.ok(loginResponseDto);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid SignUpRequestDto signUpRequestDto, HttpServletRequest request) {
        Account createdAccount = accountService.register(signUpRequestDto,request);

        return ResponseEntity.created(URI.create("/users/" + createdAccount.getId())).body("User registered successfully. Please check your email to verify your account.");
    }

    @GetMapping("/verifyEmail")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        Optional<VerificationToken> theToken = tokenService.findByToken(token);

        if (theToken.isPresent() && theToken.get().getAccount().getIsActive()) {
            return ResponseEntity.ok("the user already active");
        }

        return tokenService.validateToken(token);
    }

    @PostMapping("/forgot-password-request")
    public ResponseEntity<String> resetPasswordRequest(@RequestParam String email, HttpServletRequest request) {
        Account account = accountService.findByEmail(email);
        accountService.resetPasswordRequest(account, request);

        // Assuming the service method completes without exceptions, return success response.
        return ResponseEntity.ok("Password reset email sent successfully");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam @Valid @ValidPassword String password) {
        String resetResult = accountService.resetPassword(token, password);
        HttpStatus status = resetResult.equals("Password reset successfully") ? HttpStatus.ACCEPTED : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(resetResult);
    }
    

    @PutMapping("/update-account")
    public ResponseEntity<Account> updateAccountInfo(@RequestBody UpdateAccountDto updateAccountDto){

        accountService.updateAccount(updateAccountDto);

        return ResponseEntity.noContent().build();

    }


}

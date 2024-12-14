package com.auction.usersmanagement.controller;

import com.auction.usersmanagement.model.SysAccount;
import com.auction.usersmanagement.service.interfaces.IAccountService;
import com.auction.usersmanagement.security.UserAuthenticationProvider;
import com.auction.usersmanagement.dto.LoginRequestDto;
import com.auction.usersmanagement.dto.LoginResponseDto;
import com.auction.usersmanagement.dto.SignUpRequestDto;
import com.auction.usersmanagement.dto.UpdateAccountDto;
import com.auction.common.utility.AccountUtil;
import com.auction.usersmanagement.validation.customAnnotations.ValidPassword;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RequiredArgsConstructor
@RestController
@Validated
public class AccountController {

    private final IAccountService accountService;
    private final UserAuthenticationProvider userAuthenticationProvider;


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        SysAccount sysAccount = accountService.login(loginRequestDto);

        LoginResponseDto loginResponseDto =new LoginResponseDto();
        loginResponseDto.setStatus("success");
        loginResponseDto.setMessage("Login successful");

        String token=userAuthenticationProvider.createToken(sysAccount);

        loginResponseDto.setToken(token);
        return ResponseEntity.ok(loginResponseDto);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid SignUpRequestDto signUpRequestDto, HttpServletRequest request) {
        SysAccount createdSysAccount = accountService.register(signUpRequestDto,request);

        return ResponseEntity.created(URI.create("/users/" + createdSysAccount.getId())).body("User registered successfully. Please check your email to verify your account.");
    }

    @GetMapping("/reSendVerifyEmail")
    public ResponseEntity<String> reSendVerifyEmail(@RequestParam("token") String token , HttpServletRequest request) {

        accountService.reSendVerifyEmail(token,request);

        return ResponseEntity.ok("Verification email has been resent successfully.");
    }

    @GetMapping("/verifyEmail")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        return accountService.activateUser(token);
    }

    @PostMapping("/forgot-password-request")
    public ResponseEntity<String> resetPasswordRequest(@RequestParam String email, HttpServletRequest request) {
        SysAccount sysAccount = accountService.findByEmail(email);
        accountService.resetPasswordRequest(sysAccount, request);

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
    public ResponseEntity<String> updateAccountInfo(@RequestBody UpdateAccountDto updateAccountDto){

        SysAccount account = accountService.updateAccount(updateAccountDto);

        String token=userAuthenticationProvider.createToken(account);

        return ResponseEntity.ok(token);
    }


}

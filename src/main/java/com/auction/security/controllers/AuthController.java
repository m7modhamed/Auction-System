package com.auction.security.controllers;

import com.auction.Dtos.UserDto;
import com.auction.security.config.UserAuthenticationProvider;
import com.auction.security.dtos.CredentialsDto;
import com.auction.security.dtos.SignUpDto;
import com.auction.security.dtos.UserAuthDto;
import com.auction.security.entites.Account;
import com.auction.security.event.listener.RegistrationCompleteEventListener;
import com.auction.security.mappers.UserMapper;
import com.auction.security.password.IpasswordResetTokenService;
import com.auction.security.repositories.AccountRepository;
import com.auction.security.token.VerificationToken;
import com.auction.security.token.VerificationTokenService;
import com.auction.security.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AccountRepository accountRepository;
    private final UserService userService;
    private final UserAuthenticationProvider userAuthenticationProvider;
    private final VerificationTokenService tokenService;
    private final UserMapper userMapper;
    private final RegistrationCompleteEventListener eventListener;
    private final IpasswordResetTokenService passwordResetTokenService;
    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUsers(){
        List<Account> accounts =accountRepository.findAll();

       List<UserDto> userDtos=new ArrayList<>();
       for(Account account : accounts){
           userDtos.add(userMapper.toUserDto(account));
       }

        return ResponseEntity.ok(userDtos);
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid CredentialsDto credentialsDto) {
        UserAuthDto userAuthDto = userService.login(credentialsDto);
       // userAuthDto.setToken(userAuthenticationProvider.createToken(userAuthDto));
        //return ResponseEntity.ok(userAuthDto);

        return ResponseEntity.ok(userAuthenticationProvider.createToken(userAuthDto));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid SignUpDto user, HttpServletRequest request) {
        UserAuthDto createdUser = userService.register(user,request);
       // createdUser.setToken(userAuthenticationProvider.createToken(createdUser));
        return ResponseEntity.created(URI.create("/users/" + createdUser.getId())).body("User registered successfully. Please check your email to verify your account.");
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
        Account account = userService.findByEmail(email);
        userService.resetPasswordRequest(account, request);

        // Assuming the service method completes without exceptions, return success response.
        return ResponseEntity.ok("Password reset email sent successfully");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String password) {
        String resetResult = userService.resetPassword(token, password);
        HttpStatus status = resetResult.equals("Password reset successfully") ? HttpStatus.ACCEPTED : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(resetResult);
    }
    


}

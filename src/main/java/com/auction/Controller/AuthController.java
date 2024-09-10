package com.auction.Controller;

import com.auction.Dtos.UserDto;
import com.auction.security.config.UserAuthenticationProvider;
import com.auction.Dtos.CredentialsDto;
import com.auction.Dtos.LoginResponse;
import com.auction.Dtos.SignUpDto;
import com.auction.Dtos.UserAuthDto;
import com.auction.Entity.Account;
import com.auction.event.listener.RegistrationCompleteEventListener;
import com.auction.Mappers.UserMapper;
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

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AccountRepository accountRepository;
    private final AuthService authService;
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
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid CredentialsDto credentialsDto) {
        UserAuthDto userAuthDto = authService.login(credentialsDto);

        LoginResponse loginResponse=new LoginResponse();
        loginResponse.setStatus("success");
        loginResponse.setMessage("Login successful");

        String token=userAuthenticationProvider.createToken(userAuthDto);

        loginResponse.setToken(token);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid SignUpDto user, HttpServletRequest request) {
        UserAuthDto createdUser = authService.register(user,request);
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
    


}

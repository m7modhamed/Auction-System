package com.auction.Service.Interfaces;

import com.auction.Dtos.LoginRequestDto;
import com.auction.Dtos.SignUpRequestDto;
import com.auction.Dtos.UpdateAccountDto;
import com.auction.Entity.Account;
import com.auction.Entity.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public interface IAccountService {

    Account login(LoginRequestDto loginRequestDto);

    Account register(SignUpRequestDto signUpRequestDto, HttpServletRequest request);
    Account findByEmail(String email);
    void resetPasswordRequest(Account account, HttpServletRequest request);
    String resetPassword(String token, String password);
    Optional<User> getUserById(Long userId);
    void updateAccount(UpdateAccountDto updateAccountDto);
}

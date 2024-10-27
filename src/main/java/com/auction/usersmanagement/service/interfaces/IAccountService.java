package com.auction.usersmanagement.service.interfaces;

import com.auction.usersmanagement.dto.LoginRequestDto;
import com.auction.usersmanagement.dto.SignUpRequestDto;
import com.auction.usersmanagement.dto.UpdateAccountDto;
import com.auction.usersmanagement.model.SysAccount;
import com.auction.usersmanagement.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface IAccountService {

    SysAccount login(LoginRequestDto loginRequestDto);

    SysAccount register(SignUpRequestDto signUpRequestDto, HttpServletRequest request);
    SysAccount findByEmail(String email);
    void resetPasswordRequest(SysAccount sysAccount, HttpServletRequest request);
    String resetPassword(String token, String password);
    Optional<User> getUserById(Long userId);
    void updateAccount(UpdateAccountDto updateAccountDto);

    ResponseEntity<String> activateUser(String token);

}

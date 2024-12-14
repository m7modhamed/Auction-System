package com.auction.usersmanagement.service.interfaces;

import com.auction.usersmanagement.dto.LoginRequestDto;
import com.auction.usersmanagement.dto.SignUpRequestDto;
import com.auction.usersmanagement.dto.UpdateAccountDto;
import com.auction.usersmanagement.model.SysAccount;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface IAccountService {

    SysAccount login(LoginRequestDto loginRequestDto);

    SysAccount register(SignUpRequestDto signUpRequestDto, HttpServletRequest request);
    SysAccount findByEmail(String email);
    void resetPasswordRequest(SysAccount sysAccount, HttpServletRequest request);
    String resetPassword(String token, String password);
    SysAccount getAccountById(Long accountId);
    SysAccount updateAccount(UpdateAccountDto updateAccountDto);

    ResponseEntity<String> activateUser(String token);

    void reSendVerifyEmail(String token , HttpServletRequest request);
}

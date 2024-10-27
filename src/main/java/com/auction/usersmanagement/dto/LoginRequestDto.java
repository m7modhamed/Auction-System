package com.auction.usersmanagement.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class LoginRequestDto {

    @Email
    private String login;
    private String password;
}


package com.auction.security.dtos;

import jakarta.validation.constraints.Email;

public record CredentialsDto (@Email String login, String password) { }


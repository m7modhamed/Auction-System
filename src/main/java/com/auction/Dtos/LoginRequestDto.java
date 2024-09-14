package com.auction.Dtos;

import jakarta.validation.constraints.Email;

public record LoginRequestDto(@Email String login, String password) { }


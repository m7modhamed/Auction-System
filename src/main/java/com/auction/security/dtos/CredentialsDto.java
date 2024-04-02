package com.auction.security.dtos;

public record CredentialsDto (String login, char[] password) { }
package com.auction.Dtos;


import com.auction.Entity.Image;
import com.auction.validation.customAnnotations.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequestDto(@NotBlank String firstName,
                               @NotBlank String lastName,
                               @NotBlank @Email String email,
                               @ValidPassword String password,
                               Image image) {

}

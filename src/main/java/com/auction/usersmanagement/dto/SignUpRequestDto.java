package com.auction.usersmanagement.dto;


import com.auction.usersmanagement.model.ProfileImage;
import com.auction.usersmanagement.validation.customAnnotations.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequestDto(@NotBlank String firstName,
                               @NotBlank String lastName,
                               @NotBlank @Email String email,
                               @ValidPassword String password,
                               String phoneNumber,
                               ProfileImage profileImage) {

}

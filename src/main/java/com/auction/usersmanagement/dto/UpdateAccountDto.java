package com.auction.usersmanagement.dto;

import com.auction.usersmanagement.model.ProfileImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateAccountDto {

    private String firstName;

    private String lastName;

    private ProfileImage profileImage;
}

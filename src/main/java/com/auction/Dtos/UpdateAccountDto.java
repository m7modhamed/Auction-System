package com.auction.Dtos;

import com.auction.Entity.Image;
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

    private Image image;
}

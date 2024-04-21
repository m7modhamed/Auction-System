package com.auction.Dtos;

import com.auction.Enums.Address;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestAuctionDto {

    @NotBlank
    private String title;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    @Future
    private LocalDateTime expireDate;

    private ItemDto item;

    private Address location;


    private float minBid;

    private float initialPrice;

}

package com.auction.auctionmanagement.dto;

import com.auction.auctionmanagement.validation.customAnnotations.ValidAddress;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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


    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime expireDate;

    @Valid
    private ItemDto item;


    @ValidAddress
    private String address;

    @Min(1)
    private float minBid;

    @Min(1)
    private float initialPrice;

}

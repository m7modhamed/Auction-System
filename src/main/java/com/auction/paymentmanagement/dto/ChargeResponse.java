package com.auction.paymentmanagement.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChargeResponse {

    private String id;
    private Long amount;
    private String currency;
    private String status;
    private String description;


}
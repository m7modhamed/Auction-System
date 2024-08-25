package com.auction.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChargeRequest {

    private String paymentMethodId;
    private Long amount;
    private String currency;
    private String description;



}

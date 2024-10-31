package com.auction.auctionmanagement.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AbstractionAuctionInfoDto {
    private Long id;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime expireDate;

    private float currentPrice;

    private AbstractionItemInfoDto item;
}

package com.auction.Dtos;

import com.auction.validation.customAnnotations.ValidAddress;
import com.auction.validation.customAnnotations.ValidItemStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuctionSearchCriteria {

    private String searchKey;

   //@ValidItemStatus
    private String itemStatus;

    private String category;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime beginDate;


    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime expireDate;

    //@ValidAddress
    private String location;


    private double minCurrentPrice;

    private double maxCurrentPrice;


}

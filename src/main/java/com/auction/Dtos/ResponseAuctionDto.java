package com.auction.Dtos;

import com.auction.Enums.Address;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseAuctionDto {

    private Long id;

    private boolean active;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime beginDate;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime expireDate;

    private ResponseItemDto item;

    private Address location;

    private UserDto seller;

    private float minBid;

    private float initialPrice;

    private float currentPrice;

    private List<ResponseBidDto> bids;





}

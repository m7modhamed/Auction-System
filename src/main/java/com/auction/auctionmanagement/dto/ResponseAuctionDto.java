package com.auction.auctionmanagement.dto;

import com.auction.auctionmanagement.enums.Address;
import com.auction.auctionmanagement.enums.AuctionStatus;
import com.auction.usersmanagement.dto.UserDto;
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

    private AuctionStatus status;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime beginDate;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime expireDate;

    private ResponseItemDto item;

    private Address address;

    private UserDto seller;

    private float minBid;

    private float initialPrice;

    private float currentPrice;

    private List<ResponseBidDto> bids;

    private UserDto winner;

}

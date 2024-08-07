package com.auction.Dtos;

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
public class ResponseBidDto {
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime bidTime;

    private double amount;

    private UserDto bidder;
}

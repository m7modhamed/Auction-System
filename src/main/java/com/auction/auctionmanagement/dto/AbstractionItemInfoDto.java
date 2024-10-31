package com.auction.auctionmanagement.dto;

import com.auction.auctionmanagement.model.AuctionImage;
import lombok.Data;

import java.util.List;

@Data
public class AbstractionItemInfoDto {

    private String name;

    private String description;

    private List<AuctionImage> auctionImages;


}




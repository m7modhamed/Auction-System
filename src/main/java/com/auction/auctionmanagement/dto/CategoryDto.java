package com.auction.auctionmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {


    private Long id;

    private String name;

    private String description;

    private List<String> attributes;

    private String imageUrl;
}

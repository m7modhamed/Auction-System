package com.auction.auctionmanagement.dto;

import com.auction.auctionmanagement.model.AuctionImage;
import com.auction.auctionmanagement.validation.customAnnotations.ValidItemStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseItemDto {

    @NotBlank
    private String name;

    private String description;

    private List<AuctionImage> auctionImages;

    @ValidItemStatus
    private String itemStatus;

    private CategoryDto category;

    private Map<String,String> categoryAttributes;

}

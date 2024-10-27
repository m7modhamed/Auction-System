package com.auction.auctionmanagement.dto;

import com.auction.auctionmanagement.model.AuctionImage;
import com.auction.auctionmanagement.validation.customAnnotations.ValidItemStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 700)
    private String description;

    private List<AuctionImage> auctionImages =new ArrayList<>();

    @ValidItemStatus
    private String itemStatus;

    private CategoryDto category;

    private Map<String,String> categoryAttributes;

}

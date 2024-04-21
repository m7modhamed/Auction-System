package com.auction.Dtos;

import com.auction.Enums.ItemStatus;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {

    private String name;

    private String description;

    private List<String> images;

    private ItemStatus itemStatus;

    private CategoryDto category;

}

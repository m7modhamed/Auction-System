package com.auction.Dtos;

import com.auction.Enums.ItemStatus;
import com.auction.validation.customAnnotations.ValidItemStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {

    @NotBlank
    private String name;

    private String description;

    private List<String> images;

    @ValidItemStatus
    private String itemStatus;

    private CategoryDto category;

    private Map<String,String> categoryAttributes;

}

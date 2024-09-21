package com.auction.Dtos;

import com.auction.Entity.Image;
import com.auction.validation.customAnnotations.ValidItemStatus;
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

    private List<Image> images;

    @ValidItemStatus
    private String itemStatus;

    private CategoryDto category;

    private Map<String,String> categoryAttributes;

}

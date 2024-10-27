package com.auction.auctionmanagement.Mapper;

import com.auction.auctionmanagement.dto.CategoryDto;
import com.auction.auctionmanagement.model.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ICategoryMapper {

    CategoryDto toCategoryDto(Category category);

    Category toCategory(CategoryDto categoryDto);

    List<CategoryDto> toListCategories(List<Category> categories);
}

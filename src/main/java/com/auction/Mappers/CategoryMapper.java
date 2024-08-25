package com.auction.Mappers;

import com.auction.Dtos.CategoryDto;
import com.auction.Entity.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toCategoryDto(Category category);

    Category toCategory(CategoryDto categoryDto);

    List<CategoryDto> toListCategories(List<Category> categories);
}

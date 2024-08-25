package com.auction.Service.Interfaces;

import com.auction.Dtos.CategoryDto;
import com.auction.Entity.Category;

import java.util.List;

public interface ICategoryService {

    Category CreateCategory(CategoryDto categoryDto);

    List<CategoryDto> getAllCategories();
}

package com.auction.Service.Interfaces;

import com.auction.Dtos.CategoryDto;
import com.auction.Entity.Category;

public interface ICategoryService {

    Category CreateCategory(CategoryDto categoryDto);
}

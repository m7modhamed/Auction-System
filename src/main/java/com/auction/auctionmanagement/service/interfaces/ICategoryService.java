package com.auction.auctionmanagement.service.interfaces;

import com.auction.auctionmanagement.dto.CategoryDto;
import com.auction.auctionmanagement.model.Category;
import java.util.List;

public interface ICategoryService {

    Category CreateCategory(CategoryDto categoryDto);

    List<CategoryDto> getAllCategories();
    Category getCategoryById(Long id);

}

package com.auction.auctionmanagement.service.implementation;

import com.auction.auctionmanagement.dto.CategoryDto;
import com.auction.auctionmanagement.model.Category;
import com.auction.auctionmanagement.Mapper.ICategoryMapper;
import com.auction.auctionmanagement.repository.CategoryRepository;
import com.auction.auctionmanagement.service.interfaces.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final ICategoryMapper categoryMapper;

    @Override
    public Category CreateCategory(CategoryDto categoryDto){
        Category category=categoryMapper.toCategory(categoryDto);

        return categoryRepository.save(category);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        List<Category> categories= categoryRepository.findAll();

        return categoryMapper.toListCategories(categories);
    }
}

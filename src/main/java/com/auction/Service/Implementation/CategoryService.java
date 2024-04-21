package com.auction.Service.Implementation;

import com.auction.Dtos.CategoryDto;
import com.auction.Entity.Category;
import com.auction.Mappers.CategoryMapper;
import com.auction.Repository.CategoryRepository;
import com.auction.Service.Interfaces.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public Category CreateCategory(CategoryDto categoryDto){
        Category category=categoryMapper.toCategory(categoryDto);

        return categoryRepository.save(category);
    }
}

package com.auction.Service.Implementation;

import com.auction.Dtos.CategoryDto;
import com.auction.Entity.Category;
import com.auction.Mappers.ICategoryMapper;
import com.auction.Repository.CategoryRepository;
import com.auction.Service.Interfaces.ICategoryService;
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

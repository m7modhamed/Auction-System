package com.auction.auctionmanagement.service.implementation;

import com.auction.auctionmanagement.dto.CategoryDto;
import com.auction.auctionmanagement.model.Category;
import com.auction.auctionmanagement.Mapper.ICategoryMapper;
import com.auction.auctionmanagement.repository.CategoryRepository;
import com.auction.auctionmanagement.service.interfaces.ICategoryService;
import com.auction.common.exceptions.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
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

    public Category getCategoryById(Long id){
        return categoryRepository.findById(id).orElseThrow(
                ()->new AppException("Category with ID " + id + " not found.", HttpStatus.NOT_FOUND)
        );
    }
}

package com.auction.Controller;

import com.auction.Dtos.CategoryDto;
import com.auction.Entity.Category;
import com.auction.Service.Implementation.CategoryService;
import com.auction.Service.Interfaces.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {
    private final ICategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<Category> createCategory(@RequestBody CategoryDto categoryDto){
             Category createdCategory=categoryService.CreateCategory(categoryDto);

             return ResponseEntity.created(URI.create("/")).body(createdCategory);
    }


    @GetMapping("/all")
    public ResponseEntity<List<CategoryDto>> getAllCategory(){
        List<CategoryDto> categoryList=categoryService.getAllCategories();

        return ResponseEntity.ok(categoryList);
    }


}

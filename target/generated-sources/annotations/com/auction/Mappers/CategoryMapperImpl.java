package com.auction.Mappers;

import com.auction.Dtos.CategoryDto;
import com.auction.Entity.Category;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "٢٠٢٤-٠٨-٢٥T١٨:٣٢:٢٥+0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public CategoryDto toCategoryDto(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryDto.CategoryDtoBuilder categoryDto = CategoryDto.builder();

        categoryDto.id( category.getId() );
        categoryDto.name( category.getName() );
        categoryDto.description( category.getDescription() );
        List<String> list = category.getAttributes();
        if ( list != null ) {
            categoryDto.attributes( new ArrayList<String>( list ) );
        }

        return categoryDto.build();
    }

    @Override
    public Category toCategory(CategoryDto categoryDto) {
        if ( categoryDto == null ) {
            return null;
        }

        Category category = new Category();

        category.setId( categoryDto.getId() );
        category.setName( categoryDto.getName() );
        category.setDescription( categoryDto.getDescription() );
        List<String> list = categoryDto.getAttributes();
        if ( list != null ) {
            category.setAttributes( new ArrayList<String>( list ) );
        }

        return category;
    }

    @Override
    public List<CategoryDto> toListCategories(List<Category> categories) {
        if ( categories == null ) {
            return null;
        }

        List<CategoryDto> list = new ArrayList<CategoryDto>( categories.size() );
        for ( Category category : categories ) {
            list.add( toCategoryDto( category ) );
        }

        return list;
    }
}

package com.jossbee.houseOwner.mapper;

import com.jossbee.houseOwner.dto.CategoryDto;
import com.jossbee.houseOwner.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryDto categoryToDto(Category category) {
        CategoryDto dto = new CategoryDto();

        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setImageUrl(category.getImageUrl());

        return dto;
    }

    public Category dtoToCategory(CategoryDto dto) {
        Category category = new Category();

        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setImageUrl(dto.getImageUrl());

        return category;
    }

}

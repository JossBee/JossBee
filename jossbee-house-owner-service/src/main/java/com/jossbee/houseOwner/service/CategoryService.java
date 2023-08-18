package com.jossbee.houseOwner.service;

import com.jossbee.houseOwner.converter.CategoryConverterService;
import com.jossbee.houseOwner.dto.CategoryDto;
import com.jossbee.houseOwner.model.Category;
import com.jossbee.houseOwner.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryConverterService categoryConverterService;

    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(categoryConverterService::categoryToDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryById(String id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category != null) {
            return categoryConverterService.categoryToDto(category);
        }
        return null;
    }

    public CategoryDto createCategory(CategoryDto categoryDTO) {
        Category category = categoryConverterService.dtoToCategory(categoryDTO);
        Category createdCategory = categoryRepository.save(category);
        return categoryConverterService.categoryToDto(createdCategory);
    }

    public CategoryDto updateCategory(String id, CategoryDto categoryDTO) {
        if (categoryRepository.existsById(id)) {
            Category updatedCategory = categoryConverterService.dtoToCategory(categoryDTO);
            updatedCategory.setId(id);
            Category savedCategory = categoryRepository.save(updatedCategory);
            return categoryConverterService.categoryToDto(savedCategory);
        }
        return null;
    }

    public boolean deleteCategory(String id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
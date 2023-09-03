package com.jossbee.houseOwner.service;

import com.jossbee.houseOwner.mapper.CategoryMapper;
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
    private final CategoryMapper categoryMapper;

    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(categoryMapper::categoryToDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryById(String id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category != null) {
            return categoryMapper.categoryToDto(category);
        }
        return null;
    }

    public CategoryDto createCategory(CategoryDto categoryDTO) {
        Category category = categoryMapper.dtoToCategory(categoryDTO);
        Category createdCategory = categoryRepository.save(category);
        return categoryMapper.categoryToDto(createdCategory);
    }

    public CategoryDto updateCategory(String id, CategoryDto categoryDTO) {
        if (categoryRepository.existsById(id)) {
            Category updatedCategory = categoryMapper.dtoToCategory(categoryDTO);
            updatedCategory.setId(id);
            Category savedCategory = categoryRepository.save(updatedCategory);
            return categoryMapper.categoryToDto(savedCategory);
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
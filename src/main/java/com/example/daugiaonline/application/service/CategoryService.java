package com.example.daugiaonline.application.service;

import com.example.daugiaonline.application.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto request);
    List<CategoryDto> getAllCategories();
    void deleteCategory(Long id);
}

package com.example.toyplatform_swp_project.services;


import com.example.toyplatform_swp_project.dto.CategoryDTO;

import java.util.List;

public interface ICategoryService {
    CategoryDTO addCategory(CategoryDTO dto);
    CategoryDTO updateCategory(Long id, CategoryDTO dto);
    void deleteCategory(Long id);
    List<CategoryDTO> getAllCategories();


}


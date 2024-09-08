package com.example.toyplatform_swp_project.services.implement;

import com.example.toyplatform_swp_project.dto.CategoryDTO;
import com.example.toyplatform_swp_project.model.Category;
import com.example.toyplatform_swp_project.repository.CategoryRepository;
import com.example.toyplatform_swp_project.services.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService implements ICategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public CategoryDTO addCategory(CategoryDTO dto) {
        Category category = mapDtoToEntity(dto);
        Category savedCategory = categoryRepository.save(category);
        return mapEntityToDto(savedCategory);
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO dto) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isPresent()) {
            Category existingCategory = optionalCategory.get();
            existingCategory.setName(dto.getName());
            Category updatedCategory = categoryRepository.save(existingCategory);
            return mapEntityToDto(updatedCategory);
        } else {
            throw new RuntimeException("Category not found");
        }
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(this::mapEntityToDto).toList();
    }
    private CategoryDTO mapEntityToDto(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryId(category.getCategoryId());
        dto.setName(category.getName());
        return dto;
    }

    private Category mapDtoToEntity(CategoryDTO dto) {
        Category category = new Category();
        category.setCategoryId(dto.getCategoryId());
        category.setName(dto.getName());
        return category;
    }
}

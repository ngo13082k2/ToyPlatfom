package com.example.toyplatform_swp_project.dto;

public class CategoryDTO {
    private Long categoryId;
    private String name;

    // Getters và setters
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

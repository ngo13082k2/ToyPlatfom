package com.example.toyplatform_swp_project.repository;

import com.example.toyplatform_swp_project.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}

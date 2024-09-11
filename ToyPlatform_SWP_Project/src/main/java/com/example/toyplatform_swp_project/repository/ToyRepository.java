package com.example.toyplatform_swp_project.repository;

import com.example.toyplatform_swp_project.model.Category;
import com.example.toyplatform_swp_project.model.Toy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ToyRepository  extends JpaRepository<Toy, Long> {
    List<Toy> findByCategory(Category category);
}

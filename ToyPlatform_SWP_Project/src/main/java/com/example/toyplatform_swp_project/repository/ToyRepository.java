package com.example.toyplatform_swp_project.repository;

import com.example.toyplatform_swp_project.model.Toy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToyRepository  extends JpaRepository<Toy, Long> {
}

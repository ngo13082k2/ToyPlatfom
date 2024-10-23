package com.example.toyplatform_swp_project.repository;

import com.example.toyplatform_swp_project.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long>  {
    Optional<Supplier> findByUser_UserId(Long userId);

}

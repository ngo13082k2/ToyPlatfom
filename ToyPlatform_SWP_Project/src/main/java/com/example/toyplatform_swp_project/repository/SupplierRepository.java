package com.example.toyplatform_swp_project.repository;

import com.example.toyplatform_swp_project.model.Supplier;
import com.example.toyplatform_swp_project.model.User;
import com.example.toyplatform_swp_project.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long>  {
    Optional<Supplier> findByUser_UserId(Long userId);
    Optional<Supplier> findByUser(User user);

    List<Supplier> findByUser_Role(Role role);
}

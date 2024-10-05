package com.example.toyplatform_swp_project.repository;

import com.example.toyplatform_swp_project.model.Order;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByUserUserId(Long userId);

}

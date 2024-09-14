package com.example.toyplatform_swp_project.repository;

import com.example.toyplatform_swp_project.model.Order;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {

}

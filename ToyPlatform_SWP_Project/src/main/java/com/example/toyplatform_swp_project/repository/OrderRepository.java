package com.example.toyplatform_swp_project.repository;

import com.example.toyplatform_swp_project.model.Order;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByUserUserId(Long userId);
    @Query("SELECT SUM(o.totalPrice) FROM Order o")
    Double getTotalRevenue();
    @Query("SELECT o FROM Order o WHERE o.rental.toy.supplierId = ?1 AND o.status = 'completed'")
    List<Order> findCompletedOrdersBySupplierId(Long supplierId);
    @Query("SELECT SUM(o.rental.rentalPrice) FROM Order o WHERE o.rental.toy.supplierId = ?1 AND o.status = 'completed'")
    Double calculateTotalRentalRevenueBySupplierId(Long supplierId);

}

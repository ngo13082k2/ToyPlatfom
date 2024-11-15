package com.example.toyplatform_swp_project.repository;

import com.example.toyplatform_swp_project.model.Order;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByUserUserId(Long userId);
    @Query("SELECT SUM(o.totalPrice) FROM Order o")
    Double getTotalRevenue();
    @Query("SELECT o FROM Order o WHERE o.rental.toy.supplierId = ?1 AND o.status = 'completed'")
    List<Order> findCompletedOrdersBySupplierId(Long supplierId);
    @Query("SELECT SUM(o.rental.rentalPrice) FROM Order o WHERE o.rental.toy.supplierId = ?1 AND o.status = 'completed'")
    Double calculateTotalRentalRevenueBySupplierId(Long supplierId);
    Order findByTxnRef(String txnRef);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'completed'")
    Long countCompletedOrders();
    @Query("SELECT o FROM Order o JOIN o.rental r JOIN r.toy t JOIN t.suppliers s WHERE s.supplierId = :supplierId")
    List<Order> findOrdersBySupplierId(@Param("supplierId") Long supplierId);

    List<Order> findByStatus(String completed);
}

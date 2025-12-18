package com.example.photoprintapplication1.repository;

import com.example.photoprintapplication1.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {


    List<Order> findByUserId(Long userId);


    List<Order> findByCustomerId(Long customerId);

    Long countByStatus(Order.OrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o")
    Long getTotalOrdersCount();

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o")
    BigDecimal getTotalRevenue();

    @Query("SELECT COALESCE(AVG(o.totalPrice), 0) FROM Order o")
    BigDecimal getAverageOrderValue();
}
package com.ecommerce.repository;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    // Find orders by user
    Page<Order> findByUser(User user, Pageable pageable);
    
    Page<Order> findByUserId(Long userId, Pageable pageable);
    
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // Find orders by status
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);
    
    List<Order> findByStatus(Order.OrderStatus status);
    
    // Find orders by payment status
    Page<Order> findByPaymentStatus(Order.PaymentStatus paymentStatus, Pageable pageable);
    
    // Find orders within date range
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    Page<Order> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                 @Param("endDate") LocalDateTime endDate, 
                                 Pageable pageable);
    
    // Get total sales amount
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.paymentStatus = 'PAID'")
    BigDecimal getTotalSales();
    
    // Get sales amount for date range
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.paymentStatus = 'PAID' " +
           "AND o.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal getSalesForDateRange(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);
    
    // Count orders by status
    long countByStatus(Order.OrderStatus status);
    
    // Count orders for today
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= :startOfDay AND o.createdAt < :endOfDay")
    long countTodaysOrders(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
    
    // Get today's sales
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.paymentStatus = 'PAID' " +
           "AND o.createdAt >= :startOfDay AND o.createdAt < :endOfDay")
    BigDecimal getTodaysSales(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
    
    // Find recent orders
    @Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
    List<Order> findRecentOrders(Pageable pageable);
    
    // Search orders by order number or customer name
    @Query("SELECT o FROM Order o WHERE " +
           "LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(o.user.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(o.user.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Order> searchOrders(@Param("search") String search, Pageable pageable);
    
    // Find orders by tracking number
    Optional<Order> findByTrackingNumber(String trackingNumber);
    
    // Monthly sales report - use native query for H2 compatibility
    @Query(value = "SELECT MONTH(created_at) as month, COALESCE(SUM(total_amount), 0) as total " +
           "FROM orders WHERE payment_status = 'PAID' AND YEAR(created_at) = :year " +
           "GROUP BY MONTH(created_at)", nativeQuery = true)
    List<Object[]> getMonthlySalesReport(@Param("year") int year);
    
    // Daily orders count for last N days
    @Query(value = "SELECT CAST(created_at AS DATE) as date, COUNT(*) as count FROM orders " +
           "WHERE created_at >= :startDate GROUP BY CAST(created_at AS DATE) ORDER BY date", nativeQuery = true)
    List<Object[]> getDailyOrdersCount(@Param("startDate") LocalDateTime startDate);
}


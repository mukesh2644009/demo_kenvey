package com.ecommerce.repository;

import com.ecommerce.entity.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    List<OrderItem> findByOrderId(Long orderId);
    
    List<OrderItem> findByProductId(Long productId);
    
    // Find most purchased products
    @Query("SELECT oi.product.id, oi.product.name, SUM(oi.quantity) as totalQty " +
           "FROM OrderItem oi " +
           "GROUP BY oi.product.id, oi.product.name " +
           "ORDER BY totalQty DESC")
    List<Object[]> findMostPurchasedProducts(Pageable pageable);
    
    // Find most purchased products within date range
    @Query("SELECT oi.product.id, oi.product.name, SUM(oi.quantity) as totalQty " +
           "FROM OrderItem oi WHERE oi.order.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY oi.product.id, oi.product.name " +
           "ORDER BY totalQty DESC")
    List<Object[]> findMostPurchasedProductsInPeriod(@Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate,
                                                      Pageable pageable);
}


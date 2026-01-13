package com.ecommerce.repository;

import com.ecommerce.entity.InventoryItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    
    Optional<InventoryItem> findByItemId(String itemId);
    
    boolean existsByItemId(String itemId);
    
    // Find by category
    Page<InventoryItem> findByCategoryId(Long categoryId, Pageable pageable);
    
    List<InventoryItem> findByCategoryIdOrderByCreatedAtDesc(Long categoryId);
    
    // ============ PRODUCT WARRANTY QUERIES ============
    
    // Count items with active product warranty
    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.hasProductWarranty = true AND i.productWarrantyEndDate >= :today")
    long countActiveProductWarranties(@Param("today") LocalDate today);
    
    // Find items with product warranty expiring soon
    @Query("SELECT i FROM InventoryItem i WHERE i.hasProductWarranty = true AND i.productWarrantyEndDate BETWEEN :today AND :endDate ORDER BY i.productWarrantyEndDate ASC")
    List<InventoryItem> findExpiringProductWarranties(@Param("today") LocalDate today, @Param("endDate") LocalDate endDate);
    
    // Find items with expired product warranty
    @Query("SELECT i FROM InventoryItem i WHERE i.hasProductWarranty = true AND i.productWarrantyEndDate < :today")
    List<InventoryItem> findExpiredProductWarranties(@Param("today") LocalDate today);
    
    // ============ MOTOR WARRANTY QUERIES ============
    
    // Count items with active motor warranty
    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.hasMotorWarranty = true AND i.motorWarrantyEndDate >= :today")
    long countActiveMotorWarranties(@Param("today") LocalDate today);
    
    // Find items with motor warranty expiring soon
    @Query("SELECT i FROM InventoryItem i WHERE i.hasMotorWarranty = true AND i.motorWarrantyEndDate BETWEEN :today AND :endDate ORDER BY i.motorWarrantyEndDate ASC")
    List<InventoryItem> findExpiringMotorWarranties(@Param("today") LocalDate today, @Param("endDate") LocalDate endDate);
    
    // Find items with expired motor warranty
    @Query("SELECT i FROM InventoryItem i WHERE i.hasMotorWarranty = true AND i.motorWarrantyEndDate < :today")
    List<InventoryItem> findExpiredMotorWarranties(@Param("today") LocalDate today);
    
    // ============ COMBINED WARRANTY QUERIES ============
    
    // Find items with any warranty expiring soon
    @Query("SELECT i FROM InventoryItem i WHERE " +
           "(i.hasProductWarranty = true AND i.productWarrantyEndDate BETWEEN :today AND :endDate) OR " +
           "(i.hasMotorWarranty = true AND i.motorWarrantyEndDate BETWEEN :today AND :endDate) " +
           "ORDER BY CASE WHEN i.productWarrantyEndDate < i.motorWarrantyEndDate THEN i.productWarrantyEndDate ELSE i.motorWarrantyEndDate END ASC")
    List<InventoryItem> findItemsWithAnyExpiringWarranty(@Param("today") LocalDate today, @Param("endDate") LocalDate endDate);
    
    // Search items
    @Query("SELECT i FROM InventoryItem i WHERE " +
           "LOWER(i.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.itemId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.customerName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.customerPhone) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.address) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<InventoryItem> searchItems(@Param("search") String search, Pageable pageable);
    
    // Count by category
    long countByCategoryId(Long categoryId);
    
    // Count with product warranty
    long countByHasProductWarrantyTrue();
    
    // Count with motor warranty
    long countByHasMotorWarrantyTrue();
    
    // Recent items
    @Query("SELECT i FROM InventoryItem i ORDER BY i.createdAt DESC")
    List<InventoryItem> findRecentItems(Pageable pageable);
    
    // Find by status
    List<InventoryItem> findByStatus(InventoryItem.ItemStatus status);
    
    // Count by status
    long countByStatus(InventoryItem.ItemStatus status);
}

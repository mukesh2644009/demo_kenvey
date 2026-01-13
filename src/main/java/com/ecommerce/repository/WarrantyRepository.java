package com.ecommerce.repository;

import com.ecommerce.entity.Warranty;
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
public interface WarrantyRepository extends JpaRepository<Warranty, Long> {
    
    Optional<Warranty> findByWarrantyNumber(String warrantyNumber);
    
    Optional<Warranty> findBySerialNumber(String serialNumber);
    
    // Find warranties by user
    Page<Warranty> findByUserId(Long userId, Pageable pageable);
    
    List<Warranty> findByUserId(Long userId);
    
    // Find warranties by product
    List<Warranty> findByProductId(Long productId);
    
    // Find warranties by status
    Page<Warranty> findByStatus(Warranty.WarrantyStatus status, Pageable pageable);
    
    // Find active warranties with end date >= today
    List<Warranty> findByStatusAndWarrantyEndDateGreaterThanEqual(Warranty.WarrantyStatus status, LocalDate today);
    
    // Find expiring warranties (within date range)
    List<Warranty> findByStatusAndWarrantyEndDateBetween(Warranty.WarrantyStatus status, LocalDate today, LocalDate endDate);
    
    // Find expired warranties that need status update (active but end date < today)
    List<Warranty> findByStatusAndWarrantyEndDateLessThan(Warranty.WarrantyStatus status, LocalDate today);
    
    // Count by status
    long countByStatus(Warranty.WarrantyStatus status);
    
    // Search warranties
    @Query("SELECT w FROM Warranty w WHERE " +
           "LOWER(w.warrantyNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(w.serialNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(w.product.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(w.user.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Warranty> searchWarranties(@Param("search") String search, Pageable pageable);
    
    // Find warranty by order
    List<Warranty> findByOrderId(Long orderId);
    
    // Find warranties by user ordered by end date
    List<Warranty> findByUserIdOrderByWarrantyEndDateAsc(Long userId);
    
    // Count expiring warranties between dates
    @Query("SELECT COUNT(w) FROM Warranty w WHERE w.status = 'ACTIVE' AND w.warrantyEndDate BETWEEN :startDate AND :endDate")
    long countExpiringBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Find expiring warranties between dates
    @Query("SELECT w FROM Warranty w WHERE w.status = 'ACTIVE' AND w.warrantyEndDate BETWEEN :startDate AND :endDate ORDER BY w.warrantyEndDate ASC")
    List<Warranty> findExpiringBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

package com.ecommerce.repository;

import com.ecommerce.entity.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    
    Optional<Discount> findByCode(String code);
    
    boolean existsByCode(String code);
    
    // Find active discounts
    @Query("SELECT d FROM Discount d WHERE d.active = true " +
           "AND d.validFrom <= :now AND d.validTo >= :now " +
           "AND (d.usageLimit IS NULL OR d.usageCount < d.usageLimit)")
    List<Discount> findActiveDiscounts(@Param("now") LocalDateTime now);
    
    // Find auto-apply discounts
    @Query("SELECT d FROM Discount d WHERE d.active = true AND d.autoApply = true " +
           "AND d.validFrom <= :now AND d.validTo >= :now " +
           "AND (d.usageLimit IS NULL OR d.usageCount < d.usageLimit)")
    List<Discount> findAutoApplyDiscounts(@Param("now") LocalDateTime now);
    
    // Find discounts by customer segment
    @Query("SELECT d FROM Discount d WHERE d.active = true " +
           "AND d.validFrom <= :now AND d.validTo >= :now " +
           "AND (d.customerSegment = 'ALL' OR d.customerSegment = :segment)")
    List<Discount> findDiscountsByCustomerSegment(@Param("now") LocalDateTime now, 
                                                   @Param("segment") Discount.CustomerSegment segment);
    
    // Find discounts by type
    Page<Discount> findByType(Discount.DiscountType type, Pageable pageable);
    
    // Find expiring discounts
    @Query("SELECT d FROM Discount d WHERE d.active = true AND d.validTo BETWEEN :now AND :futureDate")
    List<Discount> findExpiringDiscounts(@Param("now") LocalDateTime now, 
                                          @Param("futureDate") LocalDateTime futureDate);
    
    // Find discounts applicable to a product
    @Query("SELECT d FROM Discount d JOIN d.applicableProducts p WHERE p.id = :productId " +
           "AND d.active = true AND d.validFrom <= :now AND d.validTo >= :now")
    List<Discount> findDiscountsForProduct(@Param("productId") Long productId, 
                                            @Param("now") LocalDateTime now);
    
    // Find discounts applicable to a category
    @Query("SELECT d FROM Discount d JOIN d.applicableCategories c WHERE c.id = :categoryId " +
           "AND d.active = true AND d.validFrom <= :now AND d.validTo >= :now")
    List<Discount> findDiscountsForCategory(@Param("categoryId") Long categoryId, 
                                             @Param("now") LocalDateTime now);
    
    // Search discounts
    @Query("SELECT d FROM Discount d WHERE " +
           "LOWER(d.code) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Discount> searchDiscounts(@Param("search") String search, Pageable pageable);
}


package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "inventory_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String itemId; // Custom Item ID like "CLR-001"
    
    @Column(nullable = false)
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ItemCategory category;
    
    @Column(columnDefinition = "TEXT")
    private String itemDetails;
    
    @Column(nullable = false)
    private LocalDate dateOfPurchase;
    
    // ============ PRODUCT WARRANTY ============
    @Builder.Default
    private Boolean hasProductWarranty = false;
    
    private LocalDate productWarrantyStartDate;
    
    private LocalDate productWarrantyEndDate;
    
    private Integer productWarrantyPeriodMonths;
    
    // ============ MOTOR WARRANTY ============
    @Builder.Default
    private Boolean hasMotorWarranty = false;
    
    private LocalDate motorWarrantyStartDate;
    
    private LocalDate motorWarrantyEndDate;
    
    private Integer motorWarrantyPeriodMonths;
    
    // Customer/Location Information
    private String customerName;
    
    private String customerPhone;
    
    private String customerEmail;
    
    @Column(columnDefinition = "TEXT")
    private String address;
    
    private String city;
    
    private String state;
    
    private String pinCode;
    
    // Additional Details
    private String serialNumber;
    
    private String model;
    
    private String brand;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ItemStatus status = ItemStatus.ACTIVE;
    
    private String notes;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum ItemStatus {
        ACTIVE,
        UNDER_SERVICE,
        RETURNED,
        REPLACED,
        INACTIVE
    }
    
    // ============ PRODUCT WARRANTY METHODS ============
    
    public boolean isProductWarrantyValid() {
        if (!hasProductWarranty || productWarrantyEndDate == null) {
            return false;
        }
        return LocalDate.now().isBefore(productWarrantyEndDate) || LocalDate.now().isEqual(productWarrantyEndDate);
    }
    
    public long productWarrantyDaysRemaining() {
        if (productWarrantyEndDate == null) return 0;
        long days = ChronoUnit.DAYS.between(LocalDate.now(), productWarrantyEndDate);
        return Math.max(0, days);
    }
    
    // ============ MOTOR WARRANTY METHODS ============
    
    public boolean isMotorWarrantyValid() {
        if (!hasMotorWarranty || motorWarrantyEndDate == null) {
            return false;
        }
        return LocalDate.now().isBefore(motorWarrantyEndDate) || LocalDate.now().isEqual(motorWarrantyEndDate);
    }
    
    public long motorWarrantyDaysRemaining() {
        if (motorWarrantyEndDate == null) return 0;
        long days = ChronoUnit.DAYS.between(LocalDate.now(), motorWarrantyEndDate);
        return Math.max(0, days);
    }
    
    // ============ COMBINED WARRANTY METHODS ============
    
    // Check if any warranty exists
    public boolean hasAnyWarranty() {
        return hasProductWarranty || hasMotorWarranty;
    }
    
    // Check if any warranty is still valid
    public boolean hasAnyValidWarranty() {
        return isProductWarrantyValid() || isMotorWarrantyValid();
    }
    
    // Legacy methods for backward compatibility
    public boolean isWarrantyValid() {
        return hasAnyValidWarranty();
    }
    
    public long daysUntilWarrantyExpiry() {
        // Return the minimum days remaining from both warranties
        long productDays = productWarrantyDaysRemaining();
        long motorDays = motorWarrantyDaysRemaining();
        
        if (hasProductWarranty && hasMotorWarranty) {
            return Math.min(productDays, motorDays);
        } else if (hasProductWarranty) {
            return productDays;
        } else if (hasMotorWarranty) {
            return motorDays;
        }
        return 0;
    }
    
    // For backward compatibility
    public Boolean getUnderWarranty() {
        return hasAnyWarranty();
    }
}

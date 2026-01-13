package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "warranties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warranty {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String warrantyNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
    
    private String serialNumber;
    
    @Column(nullable = false)
    private LocalDate purchaseDate;
    
    @Column(nullable = false)
    private LocalDate warrantyStartDate;
    
    @Column(nullable = false)
    private LocalDate warrantyEndDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private WarrantyStatus status = WarrantyStatus.ACTIVE;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Builder.Default
    private Boolean claimFiled = false;
    
    private LocalDateTime lastClaimDate;
    
    private Integer claimCount;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum WarrantyStatus {
        ACTIVE,
        EXPIRED,
        CLAIMED,
        VOIDED
    }
    
    // Check if warranty is still valid
    public boolean isValid() {
        return status == WarrantyStatus.ACTIVE && 
               LocalDate.now().isBefore(warrantyEndDate);
    }
    
    // Check if warranty is expiring soon (within 30 days)
    public boolean isExpiringSoon() {
        LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(30);
        return status == WarrantyStatus.ACTIVE && 
               warrantyEndDate.isBefore(thirtyDaysFromNow) &&
               warrantyEndDate.isAfter(LocalDate.now());
    }
    
    // Days until expiry
    public long daysUntilExpiry() {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), warrantyEndDate);
    }
}


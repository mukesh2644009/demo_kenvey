package com.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "discounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Discount {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Discount code is required")
    @Column(nullable = false, unique = true)
    private String code;
    
    @NotBlank(message = "Discount name is required")
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType type;
    
    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.01", message = "Discount value must be greater than 0")
    @Column(name = "discount_value", nullable = false)
    private BigDecimal value;
    
    // Minimum order amount required to apply this discount
    private BigDecimal minimumOrderAmount;
    
    // Maximum discount amount (for percentage discounts)
    private BigDecimal maximumDiscountAmount;
    
    @Column(nullable = false)
    private LocalDateTime validFrom;
    
    @Column(nullable = false)
    private LocalDateTime validTo;
    
    // Usage limits
    private Integer usageLimit;
    
    @Builder.Default
    private Integer usageCount = 0;
    
    // Per customer usage limit
    private Integer perCustomerLimit;
    
    // Target customer segment
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CustomerSegment customerSegment = CustomerSegment.ALL;
    
    @ManyToMany
    @JoinTable(
        name = "discount_products",
        joinColumns = @JoinColumn(name = "discount_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    @Builder.Default
    private Set<Product> applicableProducts = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "discount_categories",
        joinColumns = @JoinColumn(name = "discount_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private Set<Category> applicableCategories = new HashSet<>();
    
    @Builder.Default
    private Boolean active = true;
    
    @Builder.Default
    private Boolean autoApply = false;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum DiscountType {
        PERCENTAGE,
        FIXED_AMOUNT,
        FREE_SHIPPING,
        BUY_X_GET_Y
    }
    
    public enum CustomerSegment {
        ALL,
        NEW_CUSTOMERS,
        RETURNING_CUSTOMERS,
        VIP_CUSTOMERS
    }
    
    // Check if discount is currently valid
    public boolean isCurrentlyValid() {
        LocalDateTime now = LocalDateTime.now();
        return active && 
               now.isAfter(validFrom) && 
               now.isBefore(validTo) &&
               (usageLimit == null || usageCount < usageLimit);
    }
    
    // Calculate discount amount for a given order total
    public BigDecimal calculateDiscount(BigDecimal orderTotal) {
        if (!isCurrentlyValid()) {
            return BigDecimal.ZERO;
        }
        
        if (minimumOrderAmount != null && orderTotal.compareTo(minimumOrderAmount) < 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal discountAmount;
        
        switch (type) {
            case PERCENTAGE:
                discountAmount = orderTotal.multiply(value).divide(BigDecimal.valueOf(100));
                if (maximumDiscountAmount != null && discountAmount.compareTo(maximumDiscountAmount) > 0) {
                    discountAmount = maximumDiscountAmount;
                }
                break;
            case FIXED_AMOUNT:
                discountAmount = value;
                break;
            case FREE_SHIPPING:
                discountAmount = BigDecimal.ZERO; // Handled separately
                break;
            default:
                discountAmount = BigDecimal.ZERO;
        }
        
        return discountAmount.min(orderTotal);
    }
    
    // Increment usage count
    public void incrementUsage() {
        this.usageCount++;
    }
}


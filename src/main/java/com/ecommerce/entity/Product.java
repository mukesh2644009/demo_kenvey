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
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Product name is required")
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotBlank(message = "SKU is required")
    @Column(nullable = false, unique = true)
    private String sku;
    
    private String serialNumber;
    
    @NotBlank(message = "Brand is required")
    private String brand;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Column(nullable = false)
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock cannot be negative")
    @Column(nullable = false)
    @Builder.Default
    private Integer stockQuantity = 0;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    private String color;
    
    private String size;
    
    private String imageUrl;
    
    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    @Builder.Default
    private Set<String> additionalImages = new HashSet<>();
    
    @Builder.Default
    private Integer warrantyPeriodMonths = 12;
    
    @Builder.Default
    private Boolean active = true;
    
    @Builder.Default
    private Boolean featured = false;
    
    @Builder.Default
    private Double rating = 0.0;
    
    @Builder.Default
    private Integer reviewCount = 0;
    
    @Builder.Default
    private Integer soldCount = 0;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Warranty> warranties = new HashSet<>();
    
    @ManyToMany(mappedBy = "applicableProducts")
    @Builder.Default
    private Set<Discount> discounts = new HashSet<>();
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Helper method to check stock availability
    public boolean isInStock() {
        return this.stockQuantity > 0;
    }
    
    // Helper method to reduce stock
    public void reduceStock(int quantity) {
        if (this.stockQuantity >= quantity) {
            this.stockQuantity -= quantity;
        } else {
            throw new IllegalStateException("Insufficient stock");
        }
    }
    
    // Helper method to increase stock
    public void increaseStock(int quantity) {
        this.stockQuantity += quantity;
    }
}


package com.ecommerce.dto;

import com.ecommerce.entity.Product;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    
    private Long id;
    
    @NotBlank(message = "Product name is required")
    private String name;
    
    private String description;
    
    @NotBlank(message = "SKU is required")
    private String sku;
    
    private String serialNumber;
    
    @NotBlank(message = "Brand is required")
    private String brand;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stockQuantity;
    
    private Long categoryId;
    private String categoryName;
    
    private String color;
    private String size;
    private String imageUrl;
    private Set<String> additionalImages;
    
    private Integer warrantyPeriodMonths;
    private Boolean active;
    private Boolean featured;
    private Double rating;
    private Integer reviewCount;
    private Integer soldCount;
    
    public static ProductDto fromEntity(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .sku(product.getSku())
                .serialNumber(product.getSerialNumber())
                .brand(product.getBrand())
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .stockQuantity(product.getStockQuantity())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .color(product.getColor())
                .size(product.getSize())
                .imageUrl(product.getImageUrl())
                .additionalImages(product.getAdditionalImages())
                .warrantyPeriodMonths(product.getWarrantyPeriodMonths())
                .active(product.getActive())
                .featured(product.getFeatured())
                .rating(product.getRating())
                .reviewCount(product.getReviewCount())
                .soldCount(product.getSoldCount())
                .build();
    }
}


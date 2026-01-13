package com.ecommerce.dto;

import com.ecommerce.entity.Discount;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountDto {
    
    private Long id;
    
    @NotBlank(message = "Discount code is required")
    private String code;
    
    @NotBlank(message = "Discount name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Discount type is required")
    private Discount.DiscountType type;
    
    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.01", message = "Discount value must be greater than 0")
    private BigDecimal value;
    
    private BigDecimal minimumOrderAmount;
    private BigDecimal maximumDiscountAmount;
    
    @NotNull(message = "Valid from date is required")
    private LocalDateTime validFrom;
    
    @NotNull(message = "Valid to date is required")
    private LocalDateTime validTo;
    
    private Integer usageLimit;
    private Integer usageCount;
    private Integer perCustomerLimit;
    private Discount.CustomerSegment customerSegment;
    private Set<Long> applicableProductIds;
    private Set<Long> applicableCategoryIds;
    private Boolean active;
    private Boolean autoApply;
    private Boolean isValid;
    private LocalDateTime createdAt;
    
    public static DiscountDto fromEntity(Discount discount) {
        return DiscountDto.builder()
                .id(discount.getId())
                .code(discount.getCode())
                .name(discount.getName())
                .description(discount.getDescription())
                .type(discount.getType())
                .value(discount.getValue())
                .minimumOrderAmount(discount.getMinimumOrderAmount())
                .maximumDiscountAmount(discount.getMaximumDiscountAmount())
                .validFrom(discount.getValidFrom())
                .validTo(discount.getValidTo())
                .usageLimit(discount.getUsageLimit())
                .usageCount(discount.getUsageCount())
                .perCustomerLimit(discount.getPerCustomerLimit())
                .customerSegment(discount.getCustomerSegment())
                .applicableProductIds(discount.getApplicableProducts().stream()
                        .map(p -> p.getId())
                        .collect(Collectors.toSet()))
                .applicableCategoryIds(discount.getApplicableCategories().stream()
                        .map(c -> c.getId())
                        .collect(Collectors.toSet()))
                .active(discount.getActive())
                .autoApply(discount.getAutoApply())
                .isValid(discount.isCurrentlyValid())
                .createdAt(discount.getCreatedAt())
                .build();
    }
}


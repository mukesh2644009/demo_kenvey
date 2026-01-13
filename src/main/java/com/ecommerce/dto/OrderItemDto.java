package com.ecommerce.dto;

import com.ecommerce.entity.OrderItem;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    
    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private String productColor;
    private String productSize;
    private String productImage;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    
    public static OrderItemDto fromEntity(OrderItem item) {
        return OrderItemDto.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProductName())
                .productSku(item.getProductSku())
                .productColor(item.getProductColor())
                .productSize(item.getProductSize())
                .productImage(item.getProduct().getImageUrl())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }
}


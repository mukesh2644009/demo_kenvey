package com.ecommerce.dto;

import com.ecommerce.entity.CartItem;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    
    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private String productImage;
    private String productColor;
    private String productSize;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
    private Integer availableStock;
    
    public static CartItemDto fromEntity(CartItem item) {
        BigDecimal price = item.getProduct().getPrice();
        return CartItemDto.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .productSku(item.getProduct().getSku())
                .productImage(item.getProduct().getImageUrl())
                .productColor(item.getProduct().getColor())
                .productSize(item.getProduct().getSize())
                .unitPrice(price)
                .quantity(item.getQuantity())
                .totalPrice(price.multiply(BigDecimal.valueOf(item.getQuantity())))
                .availableStock(item.getProduct().getStockQuantity())
                .build();
    }
}


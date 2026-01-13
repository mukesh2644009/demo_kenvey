package com.ecommerce.dto;

import com.ecommerce.entity.Order;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    
    private Long id;
    private String orderNumber;
    private Long userId;
    private String userName;
    private String userEmail;
    private List<OrderItemDto> items;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal shippingAmount;
    private BigDecimal totalAmount;
    private Order.OrderStatus status;
    private Order.PaymentStatus paymentStatus;
    private String paymentMethod;
    private String paymentTransactionId;
    
    // Shipping info
    private String shippingName;
    private String shippingAddress;
    private String shippingCity;
    private String shippingState;
    private String shippingZipCode;
    private String shippingCountry;
    private String shippingPhone;
    
    // Tracking
    private String trackingNumber;
    private String carrier;
    private LocalDateTime estimatedDelivery;
    private LocalDateTime actualDelivery;
    
    private String notes;
    private String discountCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static OrderDto fromEntity(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUser().getId())
                .userName(order.getUser().getName())
                .userEmail(order.getUser().getEmail())
                .items(order.getItems().stream()
                        .map(OrderItemDto::fromEntity)
                        .collect(Collectors.toList()))
                .subtotal(order.getSubtotal())
                .discountAmount(order.getDiscountAmount())
                .taxAmount(order.getTaxAmount())
                .shippingAmount(order.getShippingAmount())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .paymentMethod(order.getPaymentMethod())
                .paymentTransactionId(order.getPaymentTransactionId())
                .shippingName(order.getShippingName())
                .shippingAddress(order.getShippingAddress())
                .shippingCity(order.getShippingCity())
                .shippingState(order.getShippingState())
                .shippingZipCode(order.getShippingZipCode())
                .shippingCountry(order.getShippingCountry())
                .shippingPhone(order.getShippingPhone())
                .trackingNumber(order.getTrackingNumber())
                .carrier(order.getCarrier())
                .estimatedDelivery(order.getEstimatedDelivery())
                .actualDelivery(order.getActualDelivery())
                .notes(order.getNotes())
                .discountCode(order.getAppliedDiscount() != null ? order.getAppliedDiscount().getCode() : null)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}


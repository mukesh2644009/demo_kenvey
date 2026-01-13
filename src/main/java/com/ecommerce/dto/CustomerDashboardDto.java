package com.ecommerce.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDashboardDto {
    private Long customerId;
    private String customerNumber;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private LocalDateTime memberSince;
    private Boolean active;
    
    // Purchase Summary
    private Integer totalOrders;
    private BigDecimal totalSpent;
    private BigDecimal averageOrderValue;
    private LocalDateTime lastPurchaseDate;
    
    // Purchase History
    private List<PurchaseHistoryDto> purchaseHistory;
    
    // Active Warranties
    private List<WarrantyStatusDto> warranties;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PurchaseHistoryDto {
        private Long orderId;
        private String orderNumber;
        private LocalDateTime purchaseDateTime;
        private List<PurchaseItemDto> items;
        private BigDecimal totalValue;
        private String status;
        private String paymentStatus;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PurchaseItemDto {
        private Long productId;
        private String productName;
        private String productSku;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        private Boolean hasWarranty;
        private Long warrantyId;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WarrantyStatusDto {
        private Long warrantyId;
        private String warrantyNumber;
        private String productName;
        private String productSku;
        private String serialNumber;
        private LocalDateTime purchaseDate;
        private LocalDateTime warrantyStartDate;
        private LocalDateTime warrantyEndDate;
        private String status; // ACTIVE, EXPIRED, EXPIRING_SOON
        private Boolean isActive;
        private Long daysRemaining;
        private String orderNumber;
    }
}


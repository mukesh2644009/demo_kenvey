package com.ecommerce.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardAnalyticsDto {
    
    // Overview Stats
    private Long totalCustomers;
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private Long activeWarranties;
    private Long expiringWarranties; // Within 30 days
    private Long expiredWarranties;
    
    // Top Customers
    private List<TopCustomerDto> topCustomersByPurchases;
    private List<TopCustomerDto> topCustomersBySpend;
    
    // Recent Activity
    private List<RecentOrderDto> recentOrders;
    
    // Warranty Overview
    private List<WarrantyAlertDto> warrantyAlerts;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopCustomerDto {
        private Long customerId;
        private String customerNumber;
        private String name;
        private String email;
        private Integer totalPurchases;
        private BigDecimal totalSpent;
        private String rank;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentOrderDto {
        private Long orderId;
        private String orderNumber;
        private String customerName;
        private String customerEmail;
        private BigDecimal totalAmount;
        private String status;
        private String orderDate;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WarrantyAlertDto {
        private Long warrantyId;
        private String warrantyNumber;
        private String customerName;
        private String productName;
        private String expiryDate;
        private Long daysRemaining;
        private String alertType; // EXPIRING_SOON, EXPIRED
    }
}


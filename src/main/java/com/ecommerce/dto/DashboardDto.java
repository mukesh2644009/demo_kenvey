package com.ecommerce.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {
    
    // Order Statistics
    private Long totalOrders;
    private Long todaysOrders;
    private Long pendingOrders;
    private Long processingOrders;
    private Long shippedOrders;
    private Long deliveredOrders;
    private Long cancelledOrders;
    
    // Sales Statistics
    private BigDecimal totalSales;
    private BigDecimal todaysSales;
    private BigDecimal monthSales;
    
    // Product Statistics
    private Long totalProducts;
    private Long activeProducts;
    private Long outOfStockProducts;
    private Long lowStockProducts;
    
    // Customer Statistics
    private Long totalCustomers;
    private Long newCustomersToday;
    
    // Warranty Statistics
    private Long activeWarranties;
    private Long expiringWarranties;
    
    // Top items
    private List<ProductDto> bestSellingProducts;
    private List<UserDto> topCustomers;
    private List<OrderDto> recentOrders;
    
    // Charts data
    private Map<String, BigDecimal> monthlySales;
    private Map<String, Long> dailyOrders;
}


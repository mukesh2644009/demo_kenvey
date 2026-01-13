package com.ecommerce.service;

import com.ecommerce.dto.DashboardDto;
import com.ecommerce.dto.OrderDto;
import com.ecommerce.dto.ProductDto;
import com.ecommerce.dto.UserDto;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.Warranty;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {
    
    private final OrderService orderService;
    private final ProductService productService;
    private final UserService userService;
    private final WarrantyService warrantyService;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    
    public DashboardDto getDashboardData() {
        return DashboardDto.builder()
                // Order statistics
                .totalOrders(orderRepository.count())
                .todaysOrders(orderService.countTodaysOrders())
                .pendingOrders(orderService.countByStatus(Order.OrderStatus.PENDING))
                .processingOrders(orderService.countByStatus(Order.OrderStatus.PROCESSING))
                .shippedOrders(orderService.countByStatus(Order.OrderStatus.SHIPPED))
                .deliveredOrders(orderService.countByStatus(Order.OrderStatus.DELIVERED))
                .cancelledOrders(orderService.countByStatus(Order.OrderStatus.CANCELLED))
                
                // Sales statistics
                .totalSales(orderService.getTotalSales())
                .todaysSales(orderService.getTodaysSales())
                .monthSales(getMonthSales())
                
                // Product statistics
                .totalProducts(productRepository.count())
                .activeProducts(productService.countActiveProducts())
                .outOfStockProducts((long) productService.getOutOfStockProducts().size())
                .lowStockProducts((long) productService.getLowStockProducts(10).size())
                
                // Customer statistics
                .totalCustomers(userService.countCustomers())
                
                // Warranty statistics
                .activeWarranties(warrantyService.countByStatus(Warranty.WarrantyStatus.ACTIVE))
                .expiringWarranties(warrantyService.countExpiringSoon())
                
                // Top items
                .bestSellingProducts(getBestSellingProducts())
                .topCustomers(getTopCustomers())
                .recentOrders(getRecentOrders())
                
                // Charts data
                .monthlySales(getMonthlySalesData())
                .build();
    }
    
    private BigDecimal getMonthSales() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime now = LocalDateTime.now();
        return orderService.getSalesForPeriod(startOfMonth, now);
    }
    
    private List<ProductDto> getBestSellingProducts() {
        return productService.getBestSellingProducts(5);
    }
    
    private List<UserDto> getTopCustomers() {
        return userService.getTopCustomersBySpending(5);
    }
    
    private List<OrderDto> getRecentOrders() {
        return orderService.getRecentOrders(10);
    }
    
    private Map<String, BigDecimal> getMonthlySalesData() {
        Map<String, BigDecimal> monthlySales = new HashMap<>();
        int currentYear = Year.now().getValue();
        
        List<Object[]> salesData = orderRepository.getMonthlySalesReport(currentYear);
        
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                          "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        
        // Initialize all months with zero
        for (String month : months) {
            monthlySales.put(month, BigDecimal.ZERO);
        }
        
        // Fill in actual data
        for (Object[] row : salesData) {
            int monthNum = ((Number) row[0]).intValue();
            BigDecimal total = (BigDecimal) row[1];
            if (monthNum >= 1 && monthNum <= 12) {
                monthlySales.put(months[monthNum - 1], total);
            }
        }
        
        return monthlySales;
    }
}


package com.ecommerce.service;

import com.ecommerce.dto.CustomerDashboardDto;
import com.ecommerce.dto.DashboardAnalyticsDto;
import com.ecommerce.entity.*;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerDashboardService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final WarrantyRepository warrantyRepository;
    private final ProductRepository productRepository;

    /**
     * Search customers by ID, customer number, name, or email
     */
    public List<CustomerDashboardDto> searchCustomers(String searchTerm) {
        List<User> customers;
        
        // Try to parse as ID first
        try {
            Long id = Long.parseLong(searchTerm);
            Optional<User> user = userRepository.findById(id);
            customers = user.map(List::of).orElse(Collections.emptyList());
        } catch (NumberFormatException e) {
            // Search by name or email
            customers = userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                searchTerm, searchTerm);
        }
        
        return customers.stream()
                .filter(u -> u.getRole() == User.Role.CUSTOMER)
                .map(this::buildBasicCustomerDto)
                .collect(Collectors.toList());
    }

    /**
     * Get complete customer dashboard with all details
     */
    public CustomerDashboardDto getCustomerDashboard(Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));
        
        return buildFullCustomerDashboard(customer);
    }

    /**
     * Get dashboard analytics overview
     */
    public DashboardAnalyticsDto getDashboardAnalytics() {
        // Count stats
        long totalCustomers = userRepository.countByRole(User.Role.CUSTOMER);
        long totalOrders = orderRepository.count();
        BigDecimal totalRevenue = orderRepository.getTotalSales();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
        
        // Warranty stats
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysFromNow = today.plusDays(30);
        
        long activeWarranties = warrantyRepository.countByStatus(Warranty.WarrantyStatus.ACTIVE);
        long expiringWarranties = warrantyRepository.countExpiringBetween(today, thirtyDaysFromNow);
        long expiredWarranties = warrantyRepository.countByStatus(Warranty.WarrantyStatus.EXPIRED);
        
        // Top customers by purchases
        List<DashboardAnalyticsDto.TopCustomerDto> topByPurchases = getTopCustomersByPurchases(5);
        
        // Top customers by spend
        List<DashboardAnalyticsDto.TopCustomerDto> topBySpend = getTopCustomersBySpend(5);
        
        // Recent orders
        List<DashboardAnalyticsDto.RecentOrderDto> recentOrders = getRecentOrders(10);
        
        // Warranty alerts
        List<DashboardAnalyticsDto.WarrantyAlertDto> warrantyAlerts = getWarrantyAlerts();
        
        return DashboardAnalyticsDto.builder()
                .totalCustomers(totalCustomers)
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .activeWarranties(activeWarranties)
                .expiringWarranties(expiringWarranties)
                .expiredWarranties(expiredWarranties)
                .topCustomersByPurchases(topByPurchases)
                .topCustomersBySpend(topBySpend)
                .recentOrders(recentOrders)
                .warrantyAlerts(warrantyAlerts)
                .build();
    }

    /**
     * Export customer data as CSV
     */
    public String exportCustomerDataCsv(Long customerId) {
        CustomerDashboardDto dashboard = getCustomerDashboard(customerId);
        StringBuilder csv = new StringBuilder();
        
        // Header
        csv.append("Customer Information\n");
        csv.append("ID,Name,Email,Phone,Address,City,State,Zip,Country,Member Since\n");
        csv.append(String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                dashboard.getCustomerId(),
                dashboard.getName(),
                dashboard.getEmail(),
                dashboard.getPhone() != null ? dashboard.getPhone() : "",
                dashboard.getAddress() != null ? dashboard.getAddress() : "",
                dashboard.getCity() != null ? dashboard.getCity() : "",
                dashboard.getState() != null ? dashboard.getState() : "",
                dashboard.getZipCode() != null ? dashboard.getZipCode() : "",
                dashboard.getCountry() != null ? dashboard.getCountry() : "",
                dashboard.getMemberSince() != null ? dashboard.getMemberSince().toString() : ""));
        
        csv.append("\nPurchase Summary\n");
        csv.append("Total Orders,Total Spent,Average Order Value\n");
        csv.append(String.format("%d,%.2f,%.2f\n",
                dashboard.getTotalOrders(),
                dashboard.getTotalSpent(),
                dashboard.getAverageOrderValue()));
        
        csv.append("\nPurchase History\n");
        csv.append("Order Number,Date,Product,Quantity,Unit Price,Total,Status\n");
        for (CustomerDashboardDto.PurchaseHistoryDto order : dashboard.getPurchaseHistory()) {
            for (CustomerDashboardDto.PurchaseItemDto item : order.getItems()) {
                csv.append(String.format("%s,%s,%s,%d,%.2f,%.2f,%s\n",
                        order.getOrderNumber(),
                        order.getPurchaseDateTime().toString(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice(),
                        order.getStatus()));
            }
        }
        
        csv.append("\nWarranties\n");
        csv.append("Warranty Number,Product,Start Date,End Date,Status,Days Remaining\n");
        for (CustomerDashboardDto.WarrantyStatusDto warranty : dashboard.getWarranties()) {
            csv.append(String.format("%s,%s,%s,%s,%s,%d\n",
                    warranty.getWarrantyNumber(),
                    warranty.getProductName(),
                    warranty.getWarrantyStartDate().toString(),
                    warranty.getWarrantyEndDate().toString(),
                    warranty.getStatus(),
                    warranty.getDaysRemaining()));
        }
        
        return csv.toString();
    }

    // Private helper methods
    
    private CustomerDashboardDto buildBasicCustomerDto(User customer) {
        return CustomerDashboardDto.builder()
                .customerId(customer.getId())
                .customerNumber("CUST-" + String.format("%06d", customer.getId()))
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .totalOrders(customer.getTotalOrders())
                .totalSpent(customer.getLifetimeSpent())
                .build();
    }

    private CustomerDashboardDto buildFullCustomerDashboard(User customer) {
        // Get all orders for this customer
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(customer.getId());
        
        // Calculate stats
        BigDecimal totalSpent = orders.stream()
                .filter(o -> o.getPaymentStatus() == Order.PaymentStatus.PAID)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal avgOrderValue = orders.isEmpty() ? BigDecimal.ZERO :
                totalSpent.divide(BigDecimal.valueOf(Math.max(1, orders.size())), 2, RoundingMode.HALF_UP);
        
        LocalDateTime lastPurchase = orders.isEmpty() ? null : orders.get(0).getCreatedAt();
        
        // Build purchase history
        List<CustomerDashboardDto.PurchaseHistoryDto> purchaseHistory = orders.stream()
                .map(this::buildPurchaseHistory)
                .collect(Collectors.toList());
        
        // Get warranties
        List<Warranty> warranties = warrantyRepository.findByUserIdOrderByWarrantyEndDateAsc(customer.getId());
        List<CustomerDashboardDto.WarrantyStatusDto> warrantyStatuses = warranties.stream()
                .map(this::buildWarrantyStatus)
                .collect(Collectors.toList());
        
        return CustomerDashboardDto.builder()
                .customerId(customer.getId())
                .customerNumber("CUST-" + String.format("%06d", customer.getId()))
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .city(customer.getCity())
                .state(customer.getState())
                .zipCode(customer.getZipCode())
                .country(customer.getCountry())
                .memberSince(customer.getCreatedAt())
                .active(customer.getEnabled())
                .totalOrders(orders.size())
                .totalSpent(totalSpent)
                .averageOrderValue(avgOrderValue)
                .lastPurchaseDate(lastPurchase)
                .purchaseHistory(purchaseHistory)
                .warranties(warrantyStatuses)
                .build();
    }

    private CustomerDashboardDto.PurchaseHistoryDto buildPurchaseHistory(Order order) {
        List<CustomerDashboardDto.PurchaseItemDto> items = order.getItems().stream()
                .map(item -> CustomerDashboardDto.PurchaseItemDto.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProductName())
                        .productSku(item.getProduct().getSku())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .hasWarranty(item.getProduct().getWarrantyPeriodMonths() != null && 
                                     item.getProduct().getWarrantyPeriodMonths() > 0)
                        .build())
                .collect(Collectors.toList());
        
        return CustomerDashboardDto.PurchaseHistoryDto.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .purchaseDateTime(order.getCreatedAt())
                .items(items)
                .totalValue(order.getTotalAmount())
                .status(order.getStatus().name())
                .paymentStatus(order.getPaymentStatus().name())
                .build();
    }

    private CustomerDashboardDto.WarrantyStatusDto buildWarrantyStatus(Warranty warranty) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = warranty.getWarrantyEndDate();
        long daysRemaining = ChronoUnit.DAYS.between(today, endDate);
        
        String status;
        boolean isActive;
        if (warranty.getStatus() == Warranty.WarrantyStatus.VOIDED) {
            status = "VOIDED";
            isActive = false;
        } else if (daysRemaining < 0) {
            status = "EXPIRED";
            isActive = false;
        } else if (daysRemaining <= 30) {
            status = "EXPIRING_SOON";
            isActive = true;
        } else {
            status = "ACTIVE";
            isActive = true;
        }
        
        return CustomerDashboardDto.WarrantyStatusDto.builder()
                .warrantyId(warranty.getId())
                .warrantyNumber(warranty.getWarrantyNumber())
                .productName(warranty.getProduct().getName())
                .productSku(warranty.getProduct().getSku())
                .serialNumber(warranty.getSerialNumber())
                .purchaseDate(warranty.getPurchaseDate().atStartOfDay())
                .warrantyStartDate(warranty.getWarrantyStartDate().atStartOfDay())
                .warrantyEndDate(warranty.getWarrantyEndDate().atStartOfDay())
                .status(status)
                .isActive(isActive)
                .daysRemaining(Math.max(0, daysRemaining))
                .orderNumber(warranty.getOrder() != null ? warranty.getOrder().getOrderNumber() : null)
                .build();
    }

    private List<DashboardAnalyticsDto.TopCustomerDto> getTopCustomersByPurchases(int limit) {
        List<User> customers = userRepository.findTopCustomersByOrders(PageRequest.of(0, limit));
        return buildTopCustomerList(customers, "purchases");
    }

    private List<DashboardAnalyticsDto.TopCustomerDto> getTopCustomersBySpend(int limit) {
        List<User> customers = userRepository.findTopCustomersBySpend(PageRequest.of(0, limit));
        return buildTopCustomerList(customers, "spend");
    }

    private List<DashboardAnalyticsDto.TopCustomerDto> buildTopCustomerList(List<User> customers, String type) {
        List<DashboardAnalyticsDto.TopCustomerDto> result = new ArrayList<>();
        int rank = 1;
        for (User customer : customers) {
            result.add(DashboardAnalyticsDto.TopCustomerDto.builder()
                    .customerId(customer.getId())
                    .customerNumber("CUST-" + String.format("%06d", customer.getId()))
                    .name(customer.getName())
                    .email(customer.getEmail())
                    .totalPurchases(customer.getTotalOrders())
                    .totalSpent(customer.getLifetimeSpent() != null ? customer.getLifetimeSpent() : BigDecimal.ZERO)
                    .rank("#" + rank++)
                    .build());
        }
        return result;
    }

    private List<DashboardAnalyticsDto.RecentOrderDto> getRecentOrders(int limit) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return orderRepository.findRecentOrders(PageRequest.of(0, limit)).stream()
                .map(order -> DashboardAnalyticsDto.RecentOrderDto.builder()
                        .orderId(order.getId())
                        .orderNumber(order.getOrderNumber())
                        .customerName(order.getUser().getName())
                        .customerEmail(order.getUser().getEmail())
                        .totalAmount(order.getTotalAmount())
                        .status(order.getStatus().name())
                        .orderDate(order.getCreatedAt().format(formatter))
                        .build())
                .collect(Collectors.toList());
    }

    private List<DashboardAnalyticsDto.WarrantyAlertDto> getWarrantyAlerts() {
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysFromNow = today.plusDays(30);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        
        List<DashboardAnalyticsDto.WarrantyAlertDto> alerts = new ArrayList<>();
        
        // Get expiring warranties
        List<Warranty> expiringWarranties = warrantyRepository.findExpiringBetween(today, thirtyDaysFromNow);
        for (Warranty w : expiringWarranties) {
            long daysRemaining = ChronoUnit.DAYS.between(today, w.getWarrantyEndDate());
            alerts.add(DashboardAnalyticsDto.WarrantyAlertDto.builder()
                    .warrantyId(w.getId())
                    .warrantyNumber(w.getWarrantyNumber())
                    .customerName(w.getUser().getName())
                    .productName(w.getProduct().getName())
                    .expiryDate(w.getWarrantyEndDate().format(formatter))
                    .daysRemaining(daysRemaining)
                    .alertType("EXPIRING_SOON")
                    .build());
        }
        
        return alerts;
    }
}


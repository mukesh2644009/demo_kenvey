package com.ecommerce.service;

import com.ecommerce.dto.CartItemDto;
import com.ecommerce.dto.CheckoutRequest;
import com.ecommerce.dto.OrderDto;
import com.ecommerce.entity.*;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductService productService;
    private final UserService userService;
    private final DiscountService discountService;
    private final WarrantyService warrantyService;
    
    public OrderDto createOrder(Long userId, CheckoutRequest request) {
        User user = userService.findById(userId);
        List<CartItemDto> cartItems = cartService.getCartItems(userId);
        
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }
        
        // Create order
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .user(user)
                .shippingName(request.getShippingName())
                .shippingAddress(request.getShippingAddress())
                .shippingCity(request.getShippingCity())
                .shippingState(request.getShippingState())
                .shippingZipCode(request.getShippingZipCode())
                .shippingCountry(request.getShippingCountry())
                .shippingPhone(request.getShippingPhone())
                .paymentMethod(request.getPaymentMethod())
                .notes(request.getNotes())
                .status(Order.OrderStatus.PENDING)
                .paymentStatus(Order.PaymentStatus.PENDING)
                .build();
        
        // Add order items
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItemDto cartItem : cartItems) {
            Product product = productService.findById(cartItem.getProductId());
            
            // Validate stock
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new BadRequestException("Insufficient stock for: " + product.getName());
            }
            
            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .productName(product.getName())
                    .productSku(product.getSku())
                    .productColor(product.getColor())
                    .productSize(product.getSize())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(product.getPrice())
                    .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                    .build();
            
            order.addItem(orderItem);
            subtotal = subtotal.add(orderItem.getTotalPrice());
            
            // Reduce stock
            productService.reduceStock(product.getId(), cartItem.getQuantity());
            productService.incrementSoldCount(product.getId(), cartItem.getQuantity());
        }
        
        order.setSubtotal(subtotal);
        
        // Apply discount if provided
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.getDiscountCode() != null && !request.getDiscountCode().isEmpty()) {
            try {
                Discount discount = discountService.validateAndGetDiscount(request.getDiscountCode(), subtotal);
                discountAmount = discount.calculateDiscount(subtotal);
                order.setAppliedDiscount(discount);
                discountService.incrementUsage(discount.getId());
            } catch (Exception e) {
                // Discount not valid, continue without it
            }
        }
        
        order.setDiscountAmount(discountAmount);
        order.setTaxAmount(BigDecimal.ZERO); // Can be calculated based on region
        order.setShippingAmount(calculateShipping(subtotal));
        order.setTotalAmount(subtotal.subtract(discountAmount).add(order.getShippingAmount()));
        
        Order savedOrder = orderRepository.save(order);
        
        // Update customer stats
        userService.updateCustomerStats(userId, order.getTotalAmount());
        
        // Clear cart
        cartService.clearCart(userId);
        
        // Create warranties for products
        for (OrderItem item : savedOrder.getItems()) {
            warrantyService.createWarranty(savedOrder, item);
        }
        
        return OrderDto.fromEntity(savedOrder);
    }
    
    @Transactional(readOnly = true)
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
    }
    
    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long id) {
        return OrderDto.fromEntity(findById(id));
    }
    
    @Transactional(readOnly = true)
    public OrderDto getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));
        return OrderDto.fromEntity(order);
    }
    
    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByUser(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(OrderDto::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public Page<OrderDto> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(OrderDto::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByStatus(Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable)
                .map(OrderDto::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public Page<OrderDto> searchOrders(String search, Pageable pageable) {
        return orderRepository.searchOrders(search, pageable)
                .map(OrderDto::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public List<OrderDto> getRecentOrders(int limit) {
        return orderRepository.findRecentOrders(PageRequest.of(0, limit))
                .stream()
                .map(OrderDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public OrderDto updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = findById(orderId);
        order.setStatus(status);
        
        if (status == Order.OrderStatus.DELIVERED) {
            order.setActualDelivery(LocalDateTime.now());
        }
        
        return OrderDto.fromEntity(orderRepository.save(order));
    }
    
    public OrderDto updatePaymentStatus(Long orderId, Order.PaymentStatus status, String transactionId) {
        Order order = findById(orderId);
        order.setPaymentStatus(status);
        order.setPaymentTransactionId(transactionId);
        
        if (status == Order.PaymentStatus.PAID) {
            order.setStatus(Order.OrderStatus.CONFIRMED);
        }
        
        return OrderDto.fromEntity(orderRepository.save(order));
    }
    
    public OrderDto updateTracking(Long orderId, String trackingNumber, String carrier, LocalDateTime estimatedDelivery) {
        Order order = findById(orderId);
        order.setTrackingNumber(trackingNumber);
        order.setCarrier(carrier);
        order.setEstimatedDelivery(estimatedDelivery);
        order.setStatus(Order.OrderStatus.SHIPPED);
        return OrderDto.fromEntity(orderRepository.save(order));
    }
    
    public OrderDto cancelOrder(Long orderId) {
        Order order = findById(orderId);
        
        if (order.getStatus() == Order.OrderStatus.SHIPPED || 
            order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new BadRequestException("Cannot cancel shipped or delivered orders");
        }
        
        // Restore stock
        for (OrderItem item : order.getItems()) {
            productService.increaseStock(item.getProduct().getId(), item.getQuantity());
        }
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        
        // Void warranties
        warrantyService.voidWarrantiesByOrder(orderId);
        
        return OrderDto.fromEntity(orderRepository.save(order));
    }
    
    @Transactional(readOnly = true)
    public OrderDto trackOrder(String orderNumber) {
        return getOrderByNumber(orderNumber);
    }
    
    // Statistics methods
    @Transactional(readOnly = true)
    public BigDecimal getTotalSales() {
        BigDecimal result = orderRepository.getTotalSales();
        return result != null ? result : BigDecimal.ZERO;
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getTodaysSales() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        BigDecimal result = orderRepository.getTodaysSales(startOfDay, endOfDay);
        return result != null ? result : BigDecimal.ZERO;
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getSalesForPeriod(LocalDateTime start, LocalDateTime end) {
        BigDecimal result = orderRepository.getSalesForDateRange(start, end);
        return result != null ? result : BigDecimal.ZERO;
    }
    
    @Transactional(readOnly = true)
    public long countTodaysOrders() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return orderRepository.countTodaysOrders(startOfDay, endOfDay);
    }
    
    @Transactional(readOnly = true)
    public long countByStatus(Order.OrderStatus status) {
        return orderRepository.countByStatus(status);
    }
    
    private String generateOrderNumber() {
        return "ORD-" + LocalDate.now().toString().replace("-", "") + 
               "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private BigDecimal calculateShipping(BigDecimal subtotal) {
        // Free shipping for orders over $100
        if (subtotal.compareTo(new BigDecimal("100")) >= 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal("9.99");
    }
}


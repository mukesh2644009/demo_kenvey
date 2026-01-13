package com.ecommerce.controller;

import com.ecommerce.dto.CheckoutRequest;
import com.ecommerce.dto.OrderDto;
import com.ecommerce.dto.WarrantyDto;
import com.ecommerce.security.CustomUserPrincipal;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.WarrantyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    private final WarrantyService warrantyService;
    
    @PostMapping("/checkout")
    public ResponseEntity<OrderDto> checkout(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @Valid @RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(orderService.createOrder(user.getId(), request));
    }
    
    @GetMapping
    public ResponseEntity<Page<OrderDto>> getMyOrders(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(orderService.getOrdersByUser(user.getId(), pageable));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
    
    @GetMapping("/track/{orderNumber}")
    public ResponseEntity<OrderDto> trackOrder(@PathVariable String orderNumber) {
        return ResponseEntity.ok(orderService.trackOrder(orderNumber));
    }
    
    @GetMapping("/{orderId}/warranties")
    public ResponseEntity<List<WarrantyDto>> getOrderWarranties(@PathVariable Long orderId) {
        return ResponseEntity.ok(warrantyService.getWarrantiesByOrder(orderId));
    }
    
    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderDto> cancelOrder(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @PathVariable Long id) {
        // Verify order belongs to user (add this check in service layer for production)
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }
}


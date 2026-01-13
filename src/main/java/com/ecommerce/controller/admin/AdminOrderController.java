package com.ecommerce.controller.admin;

import com.ecommerce.dto.OrderDto;
import com.ecommerce.entity.Order;
import com.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {
    
    private final OrderService orderService;
    
    @GetMapping
    public ResponseEntity<Page<OrderDto>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<OrderDto>> getOrdersByStatus(
            @PathVariable Order.OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(orderService.getOrdersByStatus(status, pageable));
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<OrderDto>> searchOrders(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(orderService.searchOrders(q, pageable));
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Order.OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }
    
    @PatchMapping("/{id}/payment")
    public ResponseEntity<OrderDto> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam Order.PaymentStatus status,
            @RequestParam(required = false) String transactionId) {
        return ResponseEntity.ok(orderService.updatePaymentStatus(id, status, transactionId));
    }
    
    @PatchMapping("/{id}/tracking")
    public ResponseEntity<OrderDto> updateTracking(
            @PathVariable Long id,
            @RequestBody Map<String, String> trackingInfo) {
        
        String trackingNumber = trackingInfo.get("trackingNumber");
        String carrier = trackingInfo.get("carrier");
        LocalDateTime estimatedDelivery = trackingInfo.get("estimatedDelivery") != null ?
                LocalDateTime.parse(trackingInfo.get("estimatedDelivery")) : null;
        
        return ResponseEntity.ok(orderService.updateTracking(id, trackingNumber, carrier, estimatedDelivery));
    }
    
    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderDto> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }
}


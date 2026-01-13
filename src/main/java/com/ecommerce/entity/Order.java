package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String orderNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
    
    @Column(nullable = false)
    private BigDecimal subtotal;
    
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;
    
    @Builder.Default
    private BigDecimal shippingAmount = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private BigDecimal totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    private String paymentMethod;
    
    private String paymentTransactionId;
    
    // Shipping Information
    private String shippingName;
    private String shippingAddress;
    private String shippingCity;
    private String shippingState;
    private String shippingZipCode;
    private String shippingCountry;
    private String shippingPhone;
    
    // Tracking Information
    private String trackingNumber;
    private String carrier;
    private LocalDateTime estimatedDelivery;
    private LocalDateTime actualDelivery;
    
    private String notes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id")
    private Discount appliedDiscount;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        PROCESSING,
        SHIPPED,
        OUT_FOR_DELIVERY,
        DELIVERED,
        CANCELLED,
        RETURNED,
        REFUNDED
    }
    
    public enum PaymentStatus {
        PENDING,
        PAID,
        FAILED,
        REFUNDED
    }
    
    // Helper method to add order item
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
    
    // Helper method to remove order item
    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }
    
    // Calculate totals
    public void calculateTotals() {
        this.subtotal = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.totalAmount = this.subtotal
                .subtract(this.discountAmount)
                .add(this.taxAmount)
                .add(this.shippingAmount);
    }
}


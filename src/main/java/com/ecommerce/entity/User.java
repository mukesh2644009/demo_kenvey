package com.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(nullable = false, unique = true)
    private String email;
    
    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;
    
    private String phone;
    
    private String address;
    
    private String city;
    
    private String state;
    
    private String zipCode;
    
    private String country;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.CUSTOMER;
    
    @Builder.Default
    private Integer totalOrders = 0;
    
    @Builder.Default
    private BigDecimal lifetimeSpent = BigDecimal.ZERO;
    
    @Builder.Default
    private Boolean enabled = true;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Order> orders = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<CartItem> cartItems = new HashSet<>();
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum Role {
        CUSTOMER, ADMIN
    }
}


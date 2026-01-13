package com.ecommerce.dto;

import com.ecommerce.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    private Long id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    
    private String phone;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private User.Role role;
    private Integer totalOrders;
    private BigDecimal lifetimeSpent;
    private Boolean enabled;
    private LocalDateTime createdAt;
    
    public static UserDto fromEntity(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .city(user.getCity())
                .state(user.getState())
                .zipCode(user.getZipCode())
                .country(user.getCountry())
                .role(user.getRole())
                .totalOrders(user.getTotalOrders())
                .lifetimeSpent(user.getLifetimeSpent())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }
}


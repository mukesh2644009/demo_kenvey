package com.ecommerce.dto.auth;

import com.ecommerce.dto.UserDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    
    private String token;
    private String type;
    private UserDto user;
    
    public static AuthResponse of(String token, UserDto user) {
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .user(user)
                .build();
    }
}


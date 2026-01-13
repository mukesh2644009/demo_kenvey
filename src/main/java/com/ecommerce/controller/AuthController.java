package com.ecommerce.controller;

import com.ecommerce.dto.UserDto;
import com.ecommerce.dto.auth.AuthResponse;
import com.ecommerce.dto.auth.LoginRequest;
import com.ecommerce.dto.auth.RegisterRequest;
import com.ecommerce.entity.User;
import com.ecommerce.security.JwtTokenProvider;
import com.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        String token = tokenProvider.generateToken(authentication);
        User user = userService.findByEmail(request.getEmail());
        
        return ResponseEntity.ok(AuthResponse.of(token, UserDto.fromEntity(user)));
    }
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.createUser(request);
        String token = tokenProvider.generateToken(user.getEmail());
        
        return ResponseEntity.ok(AuthResponse.of(token, UserDto.fromEntity(user)));
    }
    
    @PostMapping("/register/admin")
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        User user = userService.createAdmin(request);
        String token = tokenProvider.generateToken(user.getEmail());
        
        return ResponseEntity.ok(AuthResponse.of(token, UserDto.fromEntity(user)));
    }
}


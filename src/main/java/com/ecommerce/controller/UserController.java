package com.ecommerce.controller;

import com.ecommerce.dto.UserDto;
import com.ecommerce.security.CustomUserPrincipal;
import com.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal CustomUserPrincipal user) {
        return ResponseEntity.ok(UserDto.fromEntity(userService.findById(user.getId())));
    }
    
    @PutMapping("/me")
    public ResponseEntity<UserDto> updateProfile(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(user.getId(), userDto));
    }
}


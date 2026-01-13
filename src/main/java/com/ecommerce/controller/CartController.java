package com.ecommerce.controller;

import com.ecommerce.dto.CartItemDto;
import com.ecommerce.security.CustomUserPrincipal;
import com.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final CartService cartService;
    
    @GetMapping
    public ResponseEntity<List<CartItemDto>> getCart(@AuthenticationPrincipal CustomUserPrincipal user) {
        return ResponseEntity.ok(cartService.getCartItems(user.getId()));
    }
    
    @PostMapping("/add")
    public ResponseEntity<CartItemDto> addToCart(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity) {
        return ResponseEntity.ok(cartService.addToCart(user.getId(), productId, quantity));
    }
    
    @PutMapping("/update")
    public ResponseEntity<CartItemDto> updateCartItem(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.updateCartItem(user.getId(), productId, quantity));
    }
    
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Void> removeFromCart(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @PathVariable Long productId) {
        cartService.removeFromCart(user.getId(), productId);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal CustomUserPrincipal user) {
        cartService.clearCart(user.getId());
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/total")
    public ResponseEntity<Map<String, Object>> getCartTotal(@AuthenticationPrincipal CustomUserPrincipal user) {
        BigDecimal total = cartService.getCartTotal(user.getId());
        int count = cartService.getCartItemCount(user.getId());
        return ResponseEntity.ok(Map.of("total", total, "itemCount", count));
    }
}


package com.ecommerce.controller;

import com.ecommerce.dto.DiscountDto;
import com.ecommerce.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
public class DiscountController {
    
    private final DiscountService discountService;
    
    @GetMapping("/validate/{code}")
    public ResponseEntity<Map<String, Object>> validateDiscount(
            @PathVariable String code,
            @RequestParam BigDecimal orderTotal) {
        
        try {
            discountService.validateAndGetDiscount(code, orderTotal);
            BigDecimal discountAmount = discountService.calculateDiscount(code, orderTotal);
            DiscountDto discount = discountService.getDiscountByCode(code);
            
            return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "discount", discount,
                    "discountAmount", discountAmount,
                    "finalTotal", orderTotal.subtract(discountAmount)
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "valid", false,
                    "message", e.getMessage()
            ));
        }
    }
}


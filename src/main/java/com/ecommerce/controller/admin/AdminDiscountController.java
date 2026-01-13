package com.ecommerce.controller.admin;

import com.ecommerce.dto.DiscountDto;
import com.ecommerce.service.DiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/discounts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDiscountController {
    
    private final DiscountService discountService;
    
    @GetMapping
    public ResponseEntity<Page<DiscountDto>> getAllDiscounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(discountService.getAllDiscounts(pageable));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DiscountDto> getDiscount(@PathVariable Long id) {
        return ResponseEntity.ok(discountService.getDiscountById(id));
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<DiscountDto>> getActiveDiscounts() {
        return ResponseEntity.ok(discountService.getActiveDiscounts());
    }
    
    @PostMapping
    public ResponseEntity<DiscountDto> createDiscount(@Valid @RequestBody DiscountDto discountDto) {
        return ResponseEntity.ok(discountService.createDiscount(discountDto));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DiscountDto> updateDiscount(
            @PathVariable Long id,
            @Valid @RequestBody DiscountDto discountDto) {
        return ResponseEntity.ok(discountService.updateDiscount(id, discountDto));
    }
    
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Void> toggleDiscountStatus(@PathVariable Long id) {
        discountService.toggleDiscountStatus(id);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscount(@PathVariable Long id) {
        discountService.deleteDiscount(id);
        return ResponseEntity.ok().build();
    }
}


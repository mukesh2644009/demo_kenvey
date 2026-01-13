package com.ecommerce.controller;

import com.ecommerce.dto.WarrantyDto;
import com.ecommerce.security.CustomUserPrincipal;
import com.ecommerce.service.WarrantyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warranties")
@RequiredArgsConstructor
public class WarrantyController {
    
    private final WarrantyService warrantyService;
    
    @GetMapping
    public ResponseEntity<Page<WarrantyDto>> getMyWarranties(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(warrantyService.getWarrantiesByUser(user.getId(), pageable));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<WarrantyDto> getWarranty(@PathVariable Long id) {
        return ResponseEntity.ok(warrantyService.getWarrantyById(id));
    }
    
    @GetMapping("/lookup")
    public ResponseEntity<WarrantyDto> lookupWarranty(
            @RequestParam(required = false) String warrantyNumber,
            @RequestParam(required = false) String serialNumber) {
        
        if (warrantyNumber != null && !warrantyNumber.isEmpty()) {
            return ResponseEntity.ok(warrantyService.getWarrantyByNumber(warrantyNumber));
        } else if (serialNumber != null && !serialNumber.isEmpty()) {
            return ResponseEntity.ok(warrantyService.getWarrantyBySerialNumber(serialNumber));
        }
        
        return ResponseEntity.badRequest().build();
    }
}


package com.ecommerce.controller.admin;

import com.ecommerce.dto.WarrantyDto;
import com.ecommerce.entity.Warranty;
import com.ecommerce.service.WarrantyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/warranties")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminWarrantyController {
    
    private final WarrantyService warrantyService;
    
    @GetMapping
    public ResponseEntity<Page<WarrantyDto>> getAllWarranties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(warrantyService.getAllWarranties(pageable));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<WarrantyDto> getWarranty(@PathVariable Long id) {
        return ResponseEntity.ok(warrantyService.getWarrantyById(id));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<WarrantyDto>> getWarrantiesByStatus(
            @PathVariable Warranty.WarrantyStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(warrantyService.getWarrantiesByStatus(status, pageable));
    }
    
    @GetMapping("/expiring")
    public ResponseEntity<List<WarrantyDto>> getExpiringWarranties(
            @RequestParam(defaultValue = "30") int daysAhead) {
        return ResponseEntity.ok(warrantyService.getExpiringWarranties(daysAhead));
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<WarrantyDto>> searchWarranties(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(warrantyService.searchWarranties(q, pageable));
    }
    
    @PostMapping("/{id}/void")
    public ResponseEntity<Void> voidWarranty(@PathVariable Long id) {
        warrantyService.voidWarranty(id);
        return ResponseEntity.ok().build();
    }
}


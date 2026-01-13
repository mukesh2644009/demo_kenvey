package com.ecommerce.controller.admin;

import com.ecommerce.dto.UserDto;
import com.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/customers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCustomerController {
    
    private final UserService userService;
    
    @GetMapping
    public ResponseEntity<Page<UserDto>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.getAllCustomers(pageable));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(UserDto.fromEntity(userService.findById(id)));
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<UserDto>> searchCustomers(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.searchCustomers(q, pageable));
    }
    
    @GetMapping("/top-spenders")
    public ResponseEntity<List<UserDto>> getTopSpenders(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(userService.getTopCustomersBySpending(limit));
    }
    
    @GetMapping("/frequent-buyers")
    public ResponseEntity<List<UserDto>> getFrequentBuyers(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(userService.getTopCustomersByOrderCount(limit));
    }
    
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<Void> toggleCustomerStatus(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return ResponseEntity.ok().build();
    }
}


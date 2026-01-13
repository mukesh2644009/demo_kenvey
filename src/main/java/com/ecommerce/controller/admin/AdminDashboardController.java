package com.ecommerce.controller.admin;

import com.ecommerce.dto.DashboardDto;
import com.ecommerce.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {
    
    private final DashboardService dashboardService;
    
    @GetMapping
    public ResponseEntity<DashboardDto> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboardData());
    }
}


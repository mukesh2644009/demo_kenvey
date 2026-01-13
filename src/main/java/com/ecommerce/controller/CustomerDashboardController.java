package com.ecommerce.controller;

import com.ecommerce.dto.CustomerDashboardDto;
import com.ecommerce.dto.DashboardAnalyticsDto;
import com.ecommerce.service.CustomerDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class CustomerDashboardController {

    private final CustomerDashboardService dashboardService;

    /**
     * Main dashboard page with analytics overview
     */
    @GetMapping
    public String dashboard(Model model) {
        DashboardAnalyticsDto analytics = dashboardService.getDashboardAnalytics();
        model.addAttribute("analytics", analytics);
        return "dashboard/index";
    }

    /**
     * Customer search page
     */
    @GetMapping("/customers")
    public String customerSearch(@RequestParam(required = false) String search, Model model) {
        if (search != null && !search.isBlank()) {
            List<CustomerDashboardDto> results = dashboardService.searchCustomers(search);
            model.addAttribute("customers", results);
            model.addAttribute("search", search);
        }
        return "dashboard/customers";
    }

    /**
     * Customer detail page with full dashboard
     */
    @GetMapping("/customers/{id}")
    public String customerDetail(@PathVariable Long id, Model model) {
        CustomerDashboardDto customer = dashboardService.getCustomerDashboard(id);
        model.addAttribute("customer", customer);
        return "dashboard/customer-detail";
    }

    /**
     * API endpoint for customer search (for AJAX)
     */
    @GetMapping("/api/customers/search")
    @ResponseBody
    public ResponseEntity<List<CustomerDashboardDto>> searchCustomersApi(@RequestParam String term) {
        List<CustomerDashboardDto> results = dashboardService.searchCustomers(term);
        return ResponseEntity.ok(results);
    }

    /**
     * API endpoint for customer dashboard data
     */
    @GetMapping("/api/customers/{id}")
    @ResponseBody
    public ResponseEntity<CustomerDashboardDto> getCustomerDashboardApi(@PathVariable Long id) {
        CustomerDashboardDto customer = dashboardService.getCustomerDashboard(id);
        return ResponseEntity.ok(customer);
    }

    /**
     * API endpoint for dashboard analytics
     */
    @GetMapping("/api/analytics")
    @ResponseBody
    public ResponseEntity<DashboardAnalyticsDto> getAnalyticsApi() {
        DashboardAnalyticsDto analytics = dashboardService.getDashboardAnalytics();
        return ResponseEntity.ok(analytics);
    }

    /**
     * Export customer data as CSV
     */
    @GetMapping("/customers/{id}/export/csv")
    public ResponseEntity<String> exportCustomerCsv(@PathVariable Long id) {
        String csvData = dashboardService.exportCustomerDataCsv(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=customer_" + id + "_data.csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
    }
}


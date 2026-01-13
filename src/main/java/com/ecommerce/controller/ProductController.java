package com.ecommerce.controller;

import com.ecommerce.dto.ProductDto;
import com.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(productService.getAllActiveProducts(pageable));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<ProductDto>> searchProducts(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.searchProducts(q, pageable));
    }
    
    @GetMapping("/filter")
    public ResponseEntity<Page<ProductDto>> filterProducts(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(productService.getProductsByFilters(
                brand, color, categoryId, minPrice, maxPrice, pageable));
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductDto>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId, pageable));
    }
    
    @GetMapping("/brand/{brand}")
    public ResponseEntity<Page<ProductDto>> getProductsByBrand(
            @PathVariable String brand,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getProductsByBrand(brand, pageable));
    }
    
    @GetMapping("/featured")
    public ResponseEntity<List<ProductDto>> getFeaturedProducts() {
        return ResponseEntity.ok(productService.getFeaturedProducts());
    }
    
    @GetMapping("/bestselling")
    public ResponseEntity<List<ProductDto>> getBestSellingProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(productService.getBestSellingProducts(limit));
    }
    
    @GetMapping("/brands")
    public ResponseEntity<List<String>> getAllBrands() {
        return ResponseEntity.ok(productService.getAllBrands());
    }
    
    @GetMapping("/colors")
    public ResponseEntity<List<String>> getAllColors() {
        return ResponseEntity.ok(productService.getAllColors());
    }
}


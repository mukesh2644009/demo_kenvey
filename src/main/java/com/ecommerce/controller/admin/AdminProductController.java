package com.ecommerce.controller.admin;

import com.ecommerce.dto.ProductDto;
import com.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {
    
    private final ProductService productService;
    
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(productService.getAllActiveProducts(pageable));
    }
    
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.createProduct(productDto));
    }
    
    @PostMapping("/bulk")
    public ResponseEntity<List<ProductDto>> createProductsBulk(@Valid @RequestBody List<ProductDto> products) {
        return ResponseEntity.ok(productService.createProductsBulk(products));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.updateProduct(id, productDto));
    }
    
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Void> updateStock(
            @PathVariable Long id,
            @RequestParam int quantity) {
        productService.updateStock(id, quantity);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductDto>> getLowStockProducts(
            @RequestParam(defaultValue = "10") int threshold) {
        return ResponseEntity.ok(productService.getLowStockProducts(threshold));
    }
    
    @GetMapping("/out-of-stock")
    public ResponseEntity<List<ProductDto>> getOutOfStockProducts() {
        return ResponseEntity.ok(productService.getOutOfStockProducts());
    }
}


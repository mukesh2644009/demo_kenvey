package com.ecommerce.controller;

import com.ecommerce.dto.CategoryDto;
import com.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }
    
    @GetMapping("/root")
    public ResponseEntity<List<CategoryDto>> getRootCategories() {
        return ResponseEntity.ok(categoryService.getRootCategories());
    }
    
    @GetMapping("/{parentId}/subcategories")
    public ResponseEntity<List<CategoryDto>> getSubCategories(@PathVariable Long parentId) {
        return ResponseEntity.ok(categoryService.getSubCategories(parentId));
    }
}


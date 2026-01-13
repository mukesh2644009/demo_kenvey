package com.ecommerce.controller.admin;

import com.ecommerce.dto.CategoryDto;
import com.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {
    
    private final CategoryService categoryService;
    
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
    
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok(categoryService.createCategory(categoryDto));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }
}


package com.ecommerce.repository;

import com.ecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    Optional<Category> findByName(String name);
    
    boolean existsByName(String name);
    
    // Find root categories (no parent)
    List<Category> findByParentIsNullAndActiveTrue();
    
    // Find subcategories
    List<Category> findByParentIdAndActiveTrue(Long parentId);
    
    // Find all active categories
    List<Category> findByActiveTrue();
    
    // Find categories ordered by display order
    @Query("SELECT c FROM Category c WHERE c.active = true ORDER BY c.displayOrder ASC, c.name ASC")
    List<Category> findAllActiveOrdered();
    
    // Count products in category
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId AND p.active = true")
    long countProductsByCategoryId(Long categoryId);
}


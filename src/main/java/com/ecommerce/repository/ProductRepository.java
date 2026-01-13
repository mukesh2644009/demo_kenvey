package com.ecommerce.repository;

import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    
    Optional<Product> findBySku(String sku);
    
    Optional<Product> findBySerialNumber(String serialNumber);
    
    boolean existsBySku(String sku);
    
    // Find by category
    Page<Product> findByCategory(Category category, Pageable pageable);
    
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    
    // Find by brand
    Page<Product> findByBrand(String brand, Pageable pageable);
    
    List<Product> findByBrand(String brand);
    
    // Find by color
    Page<Product> findByColor(String color, Pageable pageable);
    
    // Find active products
    Page<Product> findByActiveTrue(Pageable pageable);
    
    // Find featured products
    List<Product> findByFeaturedTrueAndActiveTrue();
    
    // Search products by name or description
    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Product> searchProducts(@Param("search") String search, Pageable pageable);
    
    // Find products by price range
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                    @Param("maxPrice") BigDecimal maxPrice, 
                                    Pageable pageable);
    
    // Find low stock products
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= :threshold AND p.active = true")
    List<Product> findLowStockProducts(@Param("threshold") int threshold);
    
    // Find out of stock products
    @Query("SELECT p FROM Product p WHERE p.stockQuantity = 0 AND p.active = true")
    List<Product> findOutOfStockProducts();
    
    // Get distinct brands
    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.active = true ORDER BY p.brand")
    List<String> findAllBrands();
    
    // Get distinct colors
    @Query("SELECT DISTINCT p.color FROM Product p WHERE p.active = true AND p.color IS NOT NULL ORDER BY p.color")
    List<String> findAllColors();
    
    // Find best selling products
    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY p.soldCount DESC")
    List<Product> findBestSellingProducts(Pageable pageable);
    
    // Find top rated products
    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY p.rating DESC")
    List<Product> findTopRatedProducts(Pageable pageable);
    
    // Count by category
    long countByCategory(Category category);
    
    // Count active products
    long countByActiveTrue();
    
    // Complex filter query
    @Query("SELECT p FROM Product p WHERE p.active = true " +
           "AND (:brand IS NULL OR p.brand = :brand) " +
           "AND (:color IS NULL OR p.color = :color) " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> findByFilters(@Param("brand") String brand,
                                 @Param("color") String color,
                                 @Param("categoryId") Long categoryId,
                                 @Param("minPrice") BigDecimal minPrice,
                                 @Param("maxPrice") BigDecimal maxPrice,
                                 Pageable pageable);
}


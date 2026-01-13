package com.ecommerce.service;

import com.ecommerce.dto.ProductDto;
import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    public ProductDto createProduct(ProductDto dto) {
        if (productRepository.existsBySku(dto.getSku())) {
            throw new BadRequestException("Product with this SKU already exists");
        }
        
        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .sku(dto.getSku())
                .serialNumber(dto.getSerialNumber())
                .brand(dto.getBrand())
                .price(dto.getPrice())
                .originalPrice(dto.getOriginalPrice())
                .stockQuantity(dto.getStockQuantity())
                .color(dto.getColor())
                .size(dto.getSize())
                .imageUrl(dto.getImageUrl())
                .warrantyPeriodMonths(dto.getWarrantyPeriodMonths() != null ? dto.getWarrantyPeriodMonths() : 12)
                .active(dto.getActive() != null ? dto.getActive() : true)
                .featured(dto.getFeatured() != null ? dto.getFeatured() : false)
                .build();
        
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getCategoryId()));
            product.setCategory(category);
        }
        
        return ProductDto.fromEntity(productRepository.save(product));
    }
    
    public List<ProductDto> createProductsBulk(List<ProductDto> products) {
        return products.stream()
                .map(this::createProduct)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }
    
    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        return ProductDto.fromEntity(findById(id));
    }
    
    @Transactional(readOnly = true)
    public Product findBySku(String sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "sku", sku));
    }
    
    @Transactional(readOnly = true)
    public Page<ProductDto> getAllActiveProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable)
                .map(ProductDto::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public Page<ProductDto> searchProducts(String search, Pageable pageable) {
        return productRepository.searchProducts(search, pageable)
                .map(ProductDto::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsByFilters(String brand, String color, Long categoryId,
                                                  BigDecimal minPrice, BigDecimal maxPrice,
                                                  Pageable pageable) {
        return productRepository.findByFilters(brand, color, categoryId, minPrice, maxPrice, pageable)
                .map(ProductDto::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable)
                .map(ProductDto::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsByBrand(String brand, Pageable pageable) {
        return productRepository.findByBrand(brand, pageable)
                .map(ProductDto::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByPriceRange(minPrice, maxPrice, pageable)
                .map(ProductDto::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public List<ProductDto> getFeaturedProducts() {
        return productRepository.findByFeaturedTrueAndActiveTrue()
                .stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductDto> getBestSellingProducts(int limit) {
        return productRepository.findBestSellingProducts(PageRequest.of(0, limit))
                .stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductDto> getLowStockProducts(int threshold) {
        return productRepository.findLowStockProducts(threshold)
                .stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductDto> getOutOfStockProducts() {
        return productRepository.findOutOfStockProducts()
                .stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<String> getAllBrands() {
        return productRepository.findAllBrands();
    }
    
    @Transactional(readOnly = true)
    public List<String> getAllColors() {
        return productRepository.findAllColors();
    }
    
    public ProductDto updateProduct(Long id, ProductDto dto) {
        Product product = findById(id);
        
        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getDescription() != null) product.setDescription(dto.getDescription());
        if (dto.getBrand() != null) product.setBrand(dto.getBrand());
        if (dto.getPrice() != null) product.setPrice(dto.getPrice());
        if (dto.getOriginalPrice() != null) product.setOriginalPrice(dto.getOriginalPrice());
        if (dto.getStockQuantity() != null) product.setStockQuantity(dto.getStockQuantity());
        if (dto.getColor() != null) product.setColor(dto.getColor());
        if (dto.getSize() != null) product.setSize(dto.getSize());
        if (dto.getImageUrl() != null) product.setImageUrl(dto.getImageUrl());
        if (dto.getWarrantyPeriodMonths() != null) product.setWarrantyPeriodMonths(dto.getWarrantyPeriodMonths());
        if (dto.getActive() != null) product.setActive(dto.getActive());
        if (dto.getFeatured() != null) product.setFeatured(dto.getFeatured());
        
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getCategoryId()));
            product.setCategory(category);
        }
        
        return ProductDto.fromEntity(productRepository.save(product));
    }
    
    public void updateStock(Long productId, int quantity) {
        Product product = findById(productId);
        product.setStockQuantity(quantity);
        productRepository.save(product);
    }
    
    public void reduceStock(Long productId, int quantity) {
        Product product = findById(productId);
        product.reduceStock(quantity);
        productRepository.save(product);
    }
    
    public void increaseStock(Long productId, int quantity) {
        Product product = findById(productId);
        product.increaseStock(quantity);
        productRepository.save(product);
    }
    
    public void incrementSoldCount(Long productId, int quantity) {
        Product product = findById(productId);
        product.setSoldCount(product.getSoldCount() + quantity);
        productRepository.save(product);
    }
    
    public void deleteProduct(Long id) {
        Product product = findById(id);
        product.setActive(false);
        productRepository.save(product);
    }
    
    @Transactional(readOnly = true)
    public long countActiveProducts() {
        return productRepository.countByActiveTrue();
    }
}


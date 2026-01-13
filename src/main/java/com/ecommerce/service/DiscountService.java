package com.ecommerce.service;

import com.ecommerce.dto.DiscountDto;
import com.ecommerce.entity.Category;
import com.ecommerce.entity.Discount;
import com.ecommerce.entity.Product;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.DiscountRepository;
import com.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscountService {
    
    private final DiscountRepository discountRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    public DiscountDto createDiscount(DiscountDto dto) {
        if (discountRepository.existsByCode(dto.getCode())) {
            throw new BadRequestException("Discount code already exists");
        }
        
        Discount discount = Discount.builder()
                .code(dto.getCode().toUpperCase())
                .name(dto.getName())
                .description(dto.getDescription())
                .type(dto.getType())
                .value(dto.getValue())
                .minimumOrderAmount(dto.getMinimumOrderAmount())
                .maximumDiscountAmount(dto.getMaximumDiscountAmount())
                .validFrom(dto.getValidFrom())
                .validTo(dto.getValidTo())
                .usageLimit(dto.getUsageLimit())
                .perCustomerLimit(dto.getPerCustomerLimit())
                .customerSegment(dto.getCustomerSegment() != null ? dto.getCustomerSegment() : Discount.CustomerSegment.ALL)
                .active(dto.getActive() != null ? dto.getActive() : true)
                .autoApply(dto.getAutoApply() != null ? dto.getAutoApply() : false)
                .build();
        
        // Set applicable products
        if (dto.getApplicableProductIds() != null && !dto.getApplicableProductIds().isEmpty()) {
            Set<Product> products = new HashSet<>();
            for (Long productId : dto.getApplicableProductIds()) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
                products.add(product);
            }
            discount.setApplicableProducts(products);
        }
        
        // Set applicable categories
        if (dto.getApplicableCategoryIds() != null && !dto.getApplicableCategoryIds().isEmpty()) {
            Set<Category> categories = new HashSet<>();
            for (Long categoryId : dto.getApplicableCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
                categories.add(category);
            }
            discount.setApplicableCategories(categories);
        }
        
        return DiscountDto.fromEntity(discountRepository.save(discount));
    }
    
    @Transactional(readOnly = true)
    public Discount findById(Long id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount", "id", id));
    }
    
    @Transactional(readOnly = true)
    public DiscountDto getDiscountById(Long id) {
        return DiscountDto.fromEntity(findById(id));
    }
    
    @Transactional(readOnly = true)
    public DiscountDto getDiscountByCode(String code) {
        Discount discount = discountRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Discount", "code", code));
        return DiscountDto.fromEntity(discount);
    }
    
    @Transactional(readOnly = true)
    public Page<DiscountDto> getAllDiscounts(Pageable pageable) {
        return discountRepository.findAll(pageable)
                .map(DiscountDto::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public List<DiscountDto> getActiveDiscounts() {
        return discountRepository.findActiveDiscounts(LocalDateTime.now())
                .stream()
                .map(DiscountDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<DiscountDto> getAutoApplyDiscounts() {
        return discountRepository.findAutoApplyDiscounts(LocalDateTime.now())
                .stream()
                .map(DiscountDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Page<DiscountDto> searchDiscounts(String search, Pageable pageable) {
        return discountRepository.searchDiscounts(search, pageable)
                .map(DiscountDto::fromEntity);
    }
    
    public Discount validateAndGetDiscount(String code, BigDecimal orderTotal) {
        Discount discount = discountRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new BadRequestException("Invalid discount code"));
        
        if (!discount.isCurrentlyValid()) {
            throw new BadRequestException("Discount code is not valid or has expired");
        }
        
        if (discount.getMinimumOrderAmount() != null && 
            orderTotal.compareTo(discount.getMinimumOrderAmount()) < 0) {
            throw new BadRequestException("Minimum order amount of $" + 
                    discount.getMinimumOrderAmount() + " required for this discount");
        }
        
        return discount;
    }
    
    public BigDecimal calculateDiscount(String code, BigDecimal orderTotal) {
        try {
            Discount discount = validateAndGetDiscount(code, orderTotal);
            return discount.calculateDiscount(orderTotal);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
    
    public DiscountDto updateDiscount(Long id, DiscountDto dto) {
        Discount discount = findById(id);
        
        if (dto.getName() != null) discount.setName(dto.getName());
        if (dto.getDescription() != null) discount.setDescription(dto.getDescription());
        if (dto.getValue() != null) discount.setValue(dto.getValue());
        if (dto.getMinimumOrderAmount() != null) discount.setMinimumOrderAmount(dto.getMinimumOrderAmount());
        if (dto.getMaximumDiscountAmount() != null) discount.setMaximumDiscountAmount(dto.getMaximumDiscountAmount());
        if (dto.getValidFrom() != null) discount.setValidFrom(dto.getValidFrom());
        if (dto.getValidTo() != null) discount.setValidTo(dto.getValidTo());
        if (dto.getUsageLimit() != null) discount.setUsageLimit(dto.getUsageLimit());
        if (dto.getPerCustomerLimit() != null) discount.setPerCustomerLimit(dto.getPerCustomerLimit());
        if (dto.getActive() != null) discount.setActive(dto.getActive());
        if (dto.getAutoApply() != null) discount.setAutoApply(dto.getAutoApply());
        
        return DiscountDto.fromEntity(discountRepository.save(discount));
    }
    
    public void incrementUsage(Long discountId) {
        Discount discount = findById(discountId);
        discount.incrementUsage();
        discountRepository.save(discount);
    }
    
    public void toggleDiscountStatus(Long id) {
        Discount discount = findById(id);
        discount.setActive(!discount.getActive());
        discountRepository.save(discount);
    }
    
    public void deleteDiscount(Long id) {
        Discount discount = findById(id);
        discount.setActive(false);
        discountRepository.save(discount);
    }
}


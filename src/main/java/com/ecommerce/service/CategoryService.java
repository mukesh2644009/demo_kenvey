package com.ecommerce.service;

import com.ecommerce.dto.CategoryDto;
import com.ecommerce.entity.Category;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public CategoryDto createCategory(CategoryDto dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new BadRequestException("Category with this name already exists");
        }
        
        Category category = Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .displayOrder(dto.getDisplayOrder())
                .build();
        
        if (dto.getParentId() != null) {
            Category parent = categoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getParentId()));
            category.setParent(parent);
        }
        
        return CategoryDto.fromEntity(categoryRepository.save(category));
    }
    
    @Transactional(readOnly = true)
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }
    
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long id) {
        Category category = findById(id);
        CategoryDto dto = CategoryDto.fromEntity(category);
        dto.setProductCount(categoryRepository.countProductsByCategoryId(id));
        return dto;
    }
    
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAllActiveOrdered()
                .stream()
                .map(CategoryDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<CategoryDto> getRootCategories() {
        return categoryRepository.findByParentIsNullAndActiveTrue()
                .stream()
                .map(CategoryDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<CategoryDto> getSubCategories(Long parentId) {
        return categoryRepository.findByParentIdAndActiveTrue(parentId)
                .stream()
                .map(CategoryDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public CategoryDto updateCategory(Long id, CategoryDto dto) {
        Category category = findById(id);
        
        if (dto.getName() != null) category.setName(dto.getName());
        if (dto.getDescription() != null) category.setDescription(dto.getDescription());
        if (dto.getImageUrl() != null) category.setImageUrl(dto.getImageUrl());
        if (dto.getActive() != null) category.setActive(dto.getActive());
        if (dto.getDisplayOrder() != null) category.setDisplayOrder(dto.getDisplayOrder());
        
        if (dto.getParentId() != null) {
            Category parent = categoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getParentId()));
            category.setParent(parent);
        }
        
        return CategoryDto.fromEntity(categoryRepository.save(category));
    }
    
    public void deleteCategory(Long id) {
        Category category = findById(id);
        category.setActive(false);
        categoryRepository.save(category);
    }
}


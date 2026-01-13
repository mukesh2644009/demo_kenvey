package com.ecommerce.dto;

import com.ecommerce.entity.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    
    private Long id;
    
    @NotBlank(message = "Category name is required")
    private String name;
    
    private String description;
    private String imageUrl;
    private Long parentId;
    private String parentName;
    private Set<CategoryDto> subCategories;
    private Boolean active;
    private Integer displayOrder;
    private Long productCount;
    
    public static CategoryDto fromEntity(Category category) {
        CategoryDto dto = CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .active(category.getActive())
                .displayOrder(category.getDisplayOrder())
                .build();
        
        if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
            dto.setSubCategories(category.getSubCategories().stream()
                    .filter(Category::getActive)
                    .map(CategoryDto::fromEntity)
                    .collect(Collectors.toSet()));
        }
        
        return dto;
    }
}


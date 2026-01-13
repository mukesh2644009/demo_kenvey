package com.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Category name is required")
    @Column(nullable = false, unique = true)
    private String name;
    
    private String description;
    
    private String imageUrl;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Category> subCategories = new HashSet<>();
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Product> products = new HashSet<>();
    
    @Builder.Default
    private Boolean active = true;
    
    private Integer displayOrder;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}


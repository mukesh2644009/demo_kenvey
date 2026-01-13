package com.ecommerce.repository;

import com.ecommerce.entity.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemCategoryRepository extends JpaRepository<ItemCategory, Long> {
    
    Optional<ItemCategory> findByName(String name);
    
    boolean existsByName(String name);
    
    List<ItemCategory> findByActiveOrderByDisplayOrderAsc(Boolean active);
    
    @Query("SELECT c FROM ItemCategory c ORDER BY c.displayOrder ASC")
    List<ItemCategory> findAllOrderByDisplayOrder();
    
    @Query("SELECT c FROM ItemCategory c LEFT JOIN FETCH c.items WHERE c.id = :id")
    Optional<ItemCategory> findByIdWithItems(Long id);
}


package com.ecommerce.repository;

import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    List<CartItem> findByUser(User user);
    
    List<CartItem> findByUserId(Long userId);
    
    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);
    
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
    
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.user.id = :userId AND c.product.id = :productId")
    void deleteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    
    // Count items in cart
    @Query("SELECT COALESCE(SUM(c.quantity), 0) FROM CartItem c WHERE c.user.id = :userId")
    int countItemsInCart(@Param("userId") Long userId);
}


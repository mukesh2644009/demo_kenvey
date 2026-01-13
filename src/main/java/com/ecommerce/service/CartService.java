package com.ecommerce.service;

import com.ecommerce.dto.CartItemDto;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final UserService userService;
    
    public CartItemDto addToCart(Long userId, Long productId, int quantity) {
        User user = userService.findById(userId);
        Product product = productService.findById(productId);
        
        if (!product.getActive()) {
            throw new BadRequestException("Product is not available");
        }
        
        if (product.getStockQuantity() < quantity) {
            throw new BadRequestException("Insufficient stock. Available: " + product.getStockQuantity());
        }
        
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId)
                .orElse(null);
        
        if (cartItem != null) {
            int newQuantity = cartItem.getQuantity() + quantity;
            if (product.getStockQuantity() < newQuantity) {
                throw new BadRequestException("Insufficient stock. Available: " + product.getStockQuantity());
            }
            cartItem.setQuantity(newQuantity);
        } else {
            cartItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(quantity)
                    .build();
        }
        
        return CartItemDto.fromEntity(cartItemRepository.save(cartItem));
    }
    
    public CartItemDto updateCartItem(Long userId, Long productId, int quantity) {
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new BadRequestException("Item not found in cart"));
        
        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
            return null;
        }
        
        Product product = cartItem.getProduct();
        if (product.getStockQuantity() < quantity) {
            throw new BadRequestException("Insufficient stock. Available: " + product.getStockQuantity());
        }
        
        cartItem.setQuantity(quantity);
        return CartItemDto.fromEntity(cartItemRepository.save(cartItem));
    }
    
    public void removeFromCart(Long userId, Long productId) {
        cartItemRepository.deleteByUserIdAndProductId(userId, productId);
    }
    
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }
    
    @Transactional(readOnly = true)
    public List<CartItemDto> getCartItems(Long userId) {
        return cartItemRepository.findByUserId(userId)
                .stream()
                .map(CartItemDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getCartTotal(Long userId) {
        return cartItemRepository.findByUserId(userId)
                .stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Transactional(readOnly = true)
    public int getCartItemCount(Long userId) {
        return cartItemRepository.countItemsInCart(userId);
    }
}


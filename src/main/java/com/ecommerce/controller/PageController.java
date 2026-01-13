package com.ecommerce.controller;

import com.ecommerce.dto.CategoryDto;
import com.ecommerce.dto.ProductDto;
import com.ecommerce.service.CategoryService;
import com.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PageController {
    
    private final ProductService productService;
    private final CategoryService categoryService;
    
    @GetMapping("/")
    public String home(Model model) {
        List<ProductDto> featuredProducts = productService.getFeaturedProducts();
        List<ProductDto> bestSellers = productService.getBestSellingProducts(8);
        List<CategoryDto> categories = categoryService.getRootCategories();
        
        model.addAttribute("featuredProducts", featuredProducts);
        model.addAttribute("bestSellers", bestSellers);
        model.addAttribute("categories", categories);
        
        return "index";
    }
    
    @GetMapping("/shop")
    public String shop(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {
        
        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ProductDto> products;
        if (search != null && !search.isEmpty()) {
            products = productService.searchProducts(search, pageable);
        } else {
            products = productService.getProductsByFilters(brand, color, category, minPrice, maxPrice, pageable);
        }
        
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("brands", productService.getAllBrands());
        model.addAttribute("colors", productService.getAllColors());
        
        // Preserve filter parameters
        model.addAttribute("currentSearch", search);
        model.addAttribute("currentBrand", brand);
        model.addAttribute("currentColor", color);
        model.addAttribute("currentCategory", category);
        model.addAttribute("currentMinPrice", minPrice);
        model.addAttribute("currentMaxPrice", maxPrice);
        model.addAttribute("currentSort", sortBy);
        model.addAttribute("currentSortDir", sortDir);
        
        return "shop";
    }
    
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        ProductDto product = productService.getProductById(id);
        List<ProductDto> relatedProducts = productService.getBestSellingProducts(4);
        
        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", relatedProducts);
        
        return "product-detail";
    }
    
    @GetMapping("/cart")
    public String cart() {
        return "cart";
    }
    
    @GetMapping("/checkout")
    public String checkout() {
        return "checkout";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/register")
    public String register() {
        return "register";
    }
    
    @GetMapping("/account")
    public String account() {
        return "account";
    }
    
    @GetMapping("/orders")
    public String orders() {
        return "orders";
    }
    
    @GetMapping("/order/{orderNumber}")
    public String orderDetail(@PathVariable String orderNumber, Model model) {
        model.addAttribute("orderNumber", orderNumber);
        return "order-detail";
    }
    
    @GetMapping("/track-order")
    public String trackOrder() {
        return "track-order";
    }
    
    @GetMapping("/warranties")
    public String warranties() {
        return "warranties";
    }
}


package com.ecommerce.controller;

import com.ecommerce.entity.InventoryItem;
import com.ecommerce.entity.ItemCategory;
import com.ecommerce.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminInventoryController {

    private final InventoryService inventoryService;

    /**
     * Admin login page
     */
    @GetMapping("/login")
    public String adminLogin() {
        return "admin/login";
    }

    /**
     * Admin home - shows categories
     */
    @GetMapping
    public String adminHome(Model model) {
        model.addAttribute("categories", inventoryService.getAllCategories());
        model.addAttribute("stats", inventoryService.getDashboardStats());
        return "admin/index";
    }

    /**
     * Dashboard - view all items
     */
    @GetMapping("/inventory-dashboard")
    public String inventoryDashboard(Model model) {
        Map<String, Object> stats = inventoryService.getDashboardStats();
        model.addAttribute("stats", stats);
        model.addAttribute("categories", inventoryService.getAllCategories());
        return "admin/inventory-dashboard";
    }

    // ============ Category Management ============

    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", inventoryService.getAllCategories());
        return "admin/categories";
    }

    @GetMapping("/categories/add")
    public String addCategoryForm(Model model) {
        model.addAttribute("category", new ItemCategory());
        return "admin/category-form";
    }

    @PostMapping("/categories/add")
    public String addCategory(@ModelAttribute ItemCategory category, RedirectAttributes redirectAttributes) {
        try {
            inventoryService.createCategory(category);
            redirectAttributes.addFlashAttribute("success", "Category added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/{id}/edit")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", inventoryService.getCategoryById(id));
        return "admin/category-form";
    }

    @PostMapping("/categories/{id}/edit")
    public String updateCategory(@PathVariable Long id, @ModelAttribute ItemCategory category, 
                                 RedirectAttributes redirectAttributes) {
        try {
            inventoryService.updateCategory(id, category);
            redirectAttributes.addFlashAttribute("success", "Category updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            inventoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("success", "Category deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    // ============ Inventory Item Management ============

    @GetMapping("/items")
    public String listItems(@RequestParam(required = false) Long category,
                           @RequestParam(required = false) String search,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "20") int size,
                           Model model) {
        Page<InventoryItem> items;
        
        if (search != null && !search.isBlank()) {
            items = inventoryService.searchItems(search, PageRequest.of(page, size, Sort.by("createdAt").descending()));
            model.addAttribute("search", search);
        } else if (category != null) {
            items = inventoryService.getItemsByCategoryPaged(category, PageRequest.of(page, size, Sort.by("createdAt").descending()));
            model.addAttribute("currentCategory", category);
        } else {
            items = inventoryService.getAllItems(PageRequest.of(page, size, Sort.by("createdAt").descending()));
        }
        
        model.addAttribute("items", items);
        model.addAttribute("categories", inventoryService.getAllCategories());
        return "admin/items";
    }

    @GetMapping("/items/add")
    public String addItemForm(@RequestParam(required = false) Long category, Model model) {
        InventoryItem item = new InventoryItem();
        item.setDateOfPurchase(LocalDate.now());
        if (category != null) {
            item.setCategory(inventoryService.getCategoryById(category));
        }
        model.addAttribute("item", item);
        model.addAttribute("categories", inventoryService.getAllCategories());
        model.addAttribute("statuses", InventoryItem.ItemStatus.values());
        return "admin/item-form";
    }

    @PostMapping("/items/add")
    public String addItem(@ModelAttribute InventoryItem item, 
                         @RequestParam Long categoryId,
                         RedirectAttributes redirectAttributes) {
        try {
            item.setCategory(inventoryService.getCategoryById(categoryId));
            inventoryService.createItem(item);
            redirectAttributes.addFlashAttribute("success", "Item added successfully! ID: " + item.getItemId());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/items/add";
        }
        return "redirect:/admin/items";
    }

    @GetMapping("/items/{id}")
    public String viewItem(@PathVariable Long id, Model model) {
        model.addAttribute("item", inventoryService.getItemById(id));
        return "admin/item-detail";
    }

    @GetMapping("/items/{id}/edit")
    public String editItemForm(@PathVariable Long id, Model model) {
        model.addAttribute("item", inventoryService.getItemById(id));
        model.addAttribute("categories", inventoryService.getAllCategories());
        model.addAttribute("statuses", InventoryItem.ItemStatus.values());
        return "admin/item-form";
    }

    @PostMapping("/items/{id}/edit")
    public String updateItem(@PathVariable Long id, 
                            @ModelAttribute InventoryItem item,
                            @RequestParam Long categoryId,
                            RedirectAttributes redirectAttributes) {
        try {
            item.setCategory(inventoryService.getCategoryById(categoryId));
            inventoryService.updateItem(id, item);
            redirectAttributes.addFlashAttribute("success", "Item updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/items/" + id;
    }

    @PostMapping("/items/{id}/delete")
    public String deleteItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            inventoryService.deleteItem(id);
            redirectAttributes.addFlashAttribute("success", "Item deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/items";
    }

    // ============ Category-specific item listing ============

    @GetMapping("/category/{id}")
    public String categoryItems(@PathVariable Long id, Model model) {
        ItemCategory category = inventoryService.getCategoryById(id);
        model.addAttribute("category", category);
        model.addAttribute("items", inventoryService.getItemsByCategory(id));
        model.addAttribute("categories", inventoryService.getAllCategories());
        return "admin/category-items";
    }
}


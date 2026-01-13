package com.ecommerce.service;

import com.ecommerce.entity.InventoryItem;
import com.ecommerce.entity.ItemCategory;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.InventoryItemRepository;
import com.ecommerce.repository.ItemCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final InventoryItemRepository itemRepository;
    private final ItemCategoryRepository categoryRepository;

    // ============ Category Operations ============

    public List<ItemCategory> getAllCategories() {
        return categoryRepository.findAllOrderByDisplayOrder();
    }

    public List<ItemCategory> getActiveCategories() {
        return categoryRepository.findByActiveOrderByDisplayOrderAsc(true);
    }

    public ItemCategory getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
    }

    public ItemCategory createCategory(ItemCategory category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new IllegalArgumentException("Category with name '" + category.getName() + "' already exists");
        }
        return categoryRepository.save(category);
    }

    public ItemCategory updateCategory(Long id, ItemCategory updated) {
        ItemCategory category = getCategoryById(id);
        category.setName(updated.getName());
        category.setDescription(updated.getDescription());
        category.setIcon(updated.getIcon());
        category.setActive(updated.getActive());
        category.setDisplayOrder(updated.getDisplayOrder());
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        ItemCategory category = getCategoryById(id);
        if (!category.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete category with existing items");
        }
        categoryRepository.delete(category);
    }

    // ============ Inventory Item Operations ============

    public Page<InventoryItem> getAllItems(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    public List<InventoryItem> getItemsByCategory(Long categoryId) {
        return itemRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId);
    }

    public Page<InventoryItem> getItemsByCategoryPaged(Long categoryId, Pageable pageable) {
        return itemRepository.findByCategoryId(categoryId, pageable);
    }

    public InventoryItem getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + id));
    }

    public InventoryItem getItemByItemId(String itemId) {
        return itemRepository.findByItemId(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with Item ID: " + itemId));
    }

    public InventoryItem createItem(InventoryItem item) {
        // Generate item ID if not provided
        if (item.getItemId() == null || item.getItemId().isBlank()) {
            item.setItemId(generateItemId(item.getCategory()));
        } else if (itemRepository.existsByItemId(item.getItemId())) {
            throw new IllegalArgumentException("Item with ID '" + item.getItemId() + "' already exists");
        }
        
        // Set product warranty dates if enabled
        if (Boolean.TRUE.equals(item.getHasProductWarranty()) && item.getProductWarrantyPeriodMonths() != null) {
            if (item.getProductWarrantyStartDate() == null) {
                item.setProductWarrantyStartDate(item.getDateOfPurchase());
            }
            if (item.getProductWarrantyEndDate() == null) {
                item.setProductWarrantyEndDate(item.getProductWarrantyStartDate().plusMonths(item.getProductWarrantyPeriodMonths()));
            }
        }
        
        // Set motor warranty dates if enabled
        if (Boolean.TRUE.equals(item.getHasMotorWarranty()) && item.getMotorWarrantyPeriodMonths() != null) {
            if (item.getMotorWarrantyStartDate() == null) {
                item.setMotorWarrantyStartDate(item.getDateOfPurchase());
            }
            if (item.getMotorWarrantyEndDate() == null) {
                item.setMotorWarrantyEndDate(item.getMotorWarrantyStartDate().plusMonths(item.getMotorWarrantyPeriodMonths()));
            }
        }
        
        return itemRepository.save(item);
    }

    public InventoryItem updateItem(Long id, InventoryItem updated) {
        InventoryItem item = getItemById(id);
        
        item.setName(updated.getName());
        item.setCategory(updated.getCategory());
        item.setItemDetails(updated.getItemDetails());
        item.setDateOfPurchase(updated.getDateOfPurchase());
        
        // Product Warranty
        item.setHasProductWarranty(updated.getHasProductWarranty());
        item.setProductWarrantyStartDate(updated.getProductWarrantyStartDate());
        item.setProductWarrantyEndDate(updated.getProductWarrantyEndDate());
        item.setProductWarrantyPeriodMonths(updated.getProductWarrantyPeriodMonths());
        
        // Motor Warranty
        item.setHasMotorWarranty(updated.getHasMotorWarranty());
        item.setMotorWarrantyStartDate(updated.getMotorWarrantyStartDate());
        item.setMotorWarrantyEndDate(updated.getMotorWarrantyEndDate());
        item.setMotorWarrantyPeriodMonths(updated.getMotorWarrantyPeriodMonths());
        
        // Customer info
        item.setCustomerName(updated.getCustomerName());
        item.setCustomerPhone(updated.getCustomerPhone());
        item.setCustomerEmail(updated.getCustomerEmail());
        item.setAddress(updated.getAddress());
        item.setCity(updated.getCity());
        item.setState(updated.getState());
        item.setPinCode(updated.getPinCode());
        
        // Other details
        item.setSerialNumber(updated.getSerialNumber());
        item.setModel(updated.getModel());
        item.setBrand(updated.getBrand());
        item.setStatus(updated.getStatus());
        item.setNotes(updated.getNotes());
        
        return itemRepository.save(item);
    }

    public void deleteItem(Long id) {
        InventoryItem item = getItemById(id);
        itemRepository.delete(item);
    }

    public Page<InventoryItem> searchItems(String search, Pageable pageable) {
        return itemRepository.searchItems(search, pageable);
    }

    // ============ Dashboard Statistics ============

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysFromNow = today.plusDays(30);
        
        // Total counts
        stats.put("totalItems", itemRepository.count());
        stats.put("totalCategories", categoryRepository.count());
        stats.put("activeItems", itemRepository.countByStatus(InventoryItem.ItemStatus.ACTIVE));
        
        // Items under warranty (either product or motor warranty active)
        long itemsWithActiveProductWarranty = itemRepository.countActiveProductWarranties(today);
        long itemsWithActiveMotorWarranty = itemRepository.countActiveMotorWarranties(today);
        stats.put("itemsUnderWarranty", Math.max(itemsWithActiveProductWarranty, itemsWithActiveMotorWarranty));
        
        // Product Warranty Stats
        long activeProductWarranties = itemRepository.countActiveProductWarranties(today);
        List<InventoryItem> expiringProductWarranties = itemRepository.findExpiringProductWarranties(today, thirtyDaysFromNow);
        List<InventoryItem> expiredProductWarranties = itemRepository.findExpiredProductWarranties(today);
        stats.put("activeProductWarranties", activeProductWarranties);
        stats.put("expiringProductWarranties", expiringProductWarranties);
        stats.put("expiringProductWarrantiesCount", expiringProductWarranties.size());
        stats.put("expiredProductWarrantiesCount", expiredProductWarranties.size());
        
        // Motor Warranty Stats
        long activeMotorWarranties = itemRepository.countActiveMotorWarranties(today);
        List<InventoryItem> expiringMotorWarranties = itemRepository.findExpiringMotorWarranties(today, thirtyDaysFromNow);
        List<InventoryItem> expiredMotorWarranties = itemRepository.findExpiredMotorWarranties(today);
        stats.put("activeMotorWarranties", activeMotorWarranties);
        stats.put("expiringMotorWarranties", expiringMotorWarranties);
        stats.put("expiringMotorWarrantiesCount", expiringMotorWarranties.size());
        stats.put("expiredMotorWarrantiesCount", expiredMotorWarranties.size());
        
        // Combined expiring warranties
        List<InventoryItem> allExpiringWarranties = itemRepository.findItemsWithAnyExpiringWarranty(today, thirtyDaysFromNow);
        stats.put("expiringWarranties", allExpiringWarranties);
        stats.put("expiringWarrantiesCount", allExpiringWarranties.size());
        
        // Recent items
        List<InventoryItem> recentItems = itemRepository.findRecentItems(PageRequest.of(0, 10));
        stats.put("recentItems", recentItems);
        
        // Category-wise counts
        List<ItemCategory> categories = categoryRepository.findAllOrderByDisplayOrder();
        Map<String, Long> categoryWiseCounts = new HashMap<>();
        for (ItemCategory category : categories) {
            categoryWiseCounts.put(category.getName(), itemRepository.countByCategoryId(category.getId()));
        }
        stats.put("categoryWiseCounts", categoryWiseCounts);
        stats.put("categories", categories);
        
        return stats;
    }

    public List<InventoryItem> getExpiringProductWarranties() {
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysFromNow = today.plusDays(30);
        return itemRepository.findExpiringProductWarranties(today, thirtyDaysFromNow);
    }

    public List<InventoryItem> getExpiringMotorWarranties() {
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysFromNow = today.plusDays(30);
        return itemRepository.findExpiringMotorWarranties(today, thirtyDaysFromNow);
    }

    public List<InventoryItem> getExpiredProductWarranties() {
        return itemRepository.findExpiredProductWarranties(LocalDate.now());
    }

    public List<InventoryItem> getExpiredMotorWarranties() {
        return itemRepository.findExpiredMotorWarranties(LocalDate.now());
    }

    // ============ Helper Methods ============

    private String generateItemId(ItemCategory category) {
        String prefix = getCategoryPrefix(category.getName());
        long count = itemRepository.countByCategoryId(category.getId()) + 1;
        return prefix + "-" + String.format("%04d", count);
    }

    private String getCategoryPrefix(String categoryName) {
        switch (categoryName.toLowerCase()) {
            case "cooler": return "CLR";
            case "chimney": return "CHM";
            case "mixer": return "MXR";
            case "geyser": return "GYS";
            case "atta chakki": return "ATC";
            case "fans": return "FAN";
            case "crockery": return "CRK";
            default: return categoryName.substring(0, Math.min(3, categoryName.length())).toUpperCase();
        }
    }
}

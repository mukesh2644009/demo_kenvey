package com.ecommerce.service;

import com.ecommerce.dto.WarrantyDto;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.entity.Warranty;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.WarrantyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WarrantyService {
    
    private final WarrantyRepository warrantyRepository;
    
    public WarrantyDto createWarranty(Order order, OrderItem orderItem) {
        LocalDate purchaseDate = LocalDate.now();
        int warrantyMonths = orderItem.getProduct().getWarrantyPeriodMonths();
        
        Warranty warranty = Warranty.builder()
                .warrantyNumber(generateWarrantyNumber())
                .product(orderItem.getProduct())
                .user(order.getUser())
                .order(order)
                .serialNumber(orderItem.getProduct().getSerialNumber())
                .purchaseDate(purchaseDate)
                .warrantyStartDate(purchaseDate)
                .warrantyEndDate(purchaseDate.plusMonths(warrantyMonths))
                .status(Warranty.WarrantyStatus.ACTIVE)
                .claimCount(0)
                .build();
        
        return WarrantyDto.fromEntity(warrantyRepository.save(warranty));
    }
    
    @Transactional(readOnly = true)
    public Warranty findById(Long id) {
        return warrantyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warranty", "id", id));
    }
    
    @Transactional(readOnly = true)
    public WarrantyDto getWarrantyById(Long id) {
        return WarrantyDto.fromEntity(findById(id));
    }
    
    @Transactional(readOnly = true)
    public WarrantyDto getWarrantyByNumber(String warrantyNumber) {
        Warranty warranty = warrantyRepository.findByWarrantyNumber(warrantyNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Warranty", "warrantyNumber", warrantyNumber));
        return WarrantyDto.fromEntity(warranty);
    }
    
    @Transactional(readOnly = true)
    public WarrantyDto getWarrantyBySerialNumber(String serialNumber) {
        Warranty warranty = warrantyRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Warranty", "serialNumber", serialNumber));
        return WarrantyDto.fromEntity(warranty);
    }
    
    @Transactional(readOnly = true)
    public Page<WarrantyDto> getWarrantiesByUser(Long userId, Pageable pageable) {
        return warrantyRepository.findByUserId(userId, pageable)
                .map(WarrantyDto::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public List<WarrantyDto> getWarrantiesByOrder(Long orderId) {
        return warrantyRepository.findByOrderId(orderId)
                .stream()
                .map(WarrantyDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Page<WarrantyDto> getAllWarranties(Pageable pageable) {
        return warrantyRepository.findAll(pageable)
                .map(WarrantyDto::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public Page<WarrantyDto> getWarrantiesByStatus(Warranty.WarrantyStatus status, Pageable pageable) {
        return warrantyRepository.findByStatus(status, pageable)
                .map(WarrantyDto::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public Page<WarrantyDto> searchWarranties(String search, Pageable pageable) {
        return warrantyRepository.searchWarranties(search, pageable)
                .map(WarrantyDto::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public List<WarrantyDto> getExpiringWarranties(int daysAhead) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(daysAhead);
        return warrantyRepository.findByStatusAndWarrantyEndDateBetween(
                Warranty.WarrantyStatus.ACTIVE, today, endDate)
                .stream()
                .map(WarrantyDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<WarrantyDto> getWarrantiesExpiringSoon() {
        LocalDate today = LocalDate.now();
        return warrantyRepository.findByStatusAndWarrantyEndDateBetween(
                Warranty.WarrantyStatus.ACTIVE, today, today.plusDays(30))
                .stream()
                .map(WarrantyDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public WarrantyDto fileClaim(Long warrantyId, String notes) {
        Warranty warranty = findById(warrantyId);
        
        if (!warranty.isValid()) {
            throw new IllegalStateException("Warranty is not valid for claims");
        }
        
        warranty.setClaimFiled(true);
        warranty.setLastClaimDate(java.time.LocalDateTime.now());
        warranty.setClaimCount(warranty.getClaimCount() + 1);
        warranty.setNotes(notes);
        
        return WarrantyDto.fromEntity(warrantyRepository.save(warranty));
    }
    
    public void voidWarranty(Long warrantyId) {
        Warranty warranty = findById(warrantyId);
        warranty.setStatus(Warranty.WarrantyStatus.VOIDED);
        warrantyRepository.save(warranty);
    }
    
    public void voidWarrantiesByOrder(Long orderId) {
        List<Warranty> warranties = warrantyRepository.findByOrderId(orderId);
        for (Warranty warranty : warranties) {
            warranty.setStatus(Warranty.WarrantyStatus.VOIDED);
            warrantyRepository.save(warranty);
        }
    }
    
    // Scheduled task to update expired warranties
    @Scheduled(cron = "0 0 1 * * *") // Run at 1 AM every day
    public void updateExpiredWarranties() {
        log.info("Running scheduled task to update expired warranties");
        LocalDate today = LocalDate.now();
        List<Warranty> expiredWarranties = warrantyRepository.findByStatusAndWarrantyEndDateLessThan(
                Warranty.WarrantyStatus.ACTIVE, today);
        for (Warranty warranty : expiredWarranties) {
            warranty.setStatus(Warranty.WarrantyStatus.EXPIRED);
            warrantyRepository.save(warranty);
        }
        log.info("Updated {} expired warranties", expiredWarranties.size());
    }
    
    @Transactional(readOnly = true)
    public long countByStatus(Warranty.WarrantyStatus status) {
        return warrantyRepository.countByStatus(status);
    }
    
    @Transactional(readOnly = true)
    public long countExpiringSoon() {
        LocalDate today = LocalDate.now();
        return warrantyRepository.findByStatusAndWarrantyEndDateBetween(
                Warranty.WarrantyStatus.ACTIVE, today, today.plusDays(30)).size();
    }
    
    private String generateWarrantyNumber() {
        return "WRN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }
}

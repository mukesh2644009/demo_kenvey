package com.ecommerce.dto;

import com.ecommerce.entity.Warranty;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarrantyDto {
    
    private Long id;
    private String warrantyNumber;
    private Long productId;
    private String productName;
    private String productSku;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long orderId;
    private String orderNumber;
    private String serialNumber;
    private LocalDate purchaseDate;
    private LocalDate warrantyStartDate;
    private LocalDate warrantyEndDate;
    private Warranty.WarrantyStatus status;
    private String notes;
    private Boolean claimFiled;
    private LocalDateTime lastClaimDate;
    private Integer claimCount;
    private Long daysRemaining;
    private Boolean expiringSoon;
    private LocalDateTime createdAt;
    
    public static WarrantyDto fromEntity(Warranty warranty) {
        return WarrantyDto.builder()
                .id(warranty.getId())
                .warrantyNumber(warranty.getWarrantyNumber())
                .productId(warranty.getProduct().getId())
                .productName(warranty.getProduct().getName())
                .productSku(warranty.getProduct().getSku())
                .userId(warranty.getUser().getId())
                .userName(warranty.getUser().getName())
                .userEmail(warranty.getUser().getEmail())
                .orderId(warranty.getOrder() != null ? warranty.getOrder().getId() : null)
                .orderNumber(warranty.getOrder() != null ? warranty.getOrder().getOrderNumber() : null)
                .serialNumber(warranty.getSerialNumber())
                .purchaseDate(warranty.getPurchaseDate())
                .warrantyStartDate(warranty.getWarrantyStartDate())
                .warrantyEndDate(warranty.getWarrantyEndDate())
                .status(warranty.getStatus())
                .notes(warranty.getNotes())
                .claimFiled(warranty.getClaimFiled())
                .lastClaimDate(warranty.getLastClaimDate())
                .claimCount(warranty.getClaimCount())
                .daysRemaining(warranty.daysUntilExpiry())
                .expiringSoon(warranty.isExpiringSoon())
                .createdAt(warranty.getCreatedAt())
                .build();
    }
}


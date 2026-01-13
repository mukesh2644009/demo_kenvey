package com.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckoutRequest {
    
    @NotBlank(message = "Shipping name is required")
    private String shippingName;
    
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;
    
    @NotBlank(message = "City is required")
    private String shippingCity;
    
    @NotBlank(message = "State is required")
    private String shippingState;
    
    @NotBlank(message = "Zip code is required")
    private String shippingZipCode;
    
    @NotBlank(message = "Country is required")
    private String shippingCountry;
    
    @NotBlank(message = "Phone is required")
    private String shippingPhone;
    
    private String paymentMethod;
    private String discountCode;
    private String notes;
}


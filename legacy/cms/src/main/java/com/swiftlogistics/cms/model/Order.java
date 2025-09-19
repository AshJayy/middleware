package com.swiftlogistics.cms.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    private String orderId;
    private String customerId;
    
    // Order Status Tracking
    private OrderStatus status;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime billedAt;
    
    // Billing Information
    private Double totalAmount;
    private String billingStatus;
    
    public enum OrderStatus {
        BILLED,        // CMS billing completed
        BILLING_FAILED,// CMS billing failed
        BILLING_PENDING
    }
}


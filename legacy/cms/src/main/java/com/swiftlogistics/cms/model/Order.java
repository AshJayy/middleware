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
    
    private String customerName;
    private String customerEmail;
    
    // Delivery Address
    private String deliveryAddress;
    private String city;
    private String postalCode;
    private String country;
    
    // Order Status Tracking
    private OrderStatus status;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private LocalDateTime billedAt;
    private LocalDateTime packageReadyAt;
    private LocalDateTime routedAt;
    private LocalDateTime deliveredAt;
    
    // Billing Information
    private Double totalAmount;
    private String billingStatus;
    
    // Route Information
    private List<String> waypoints;
    private String driverId;
    private String driverName;
    private String vehicleId;
    
    // Correlation ID for tracking across systems
    private String correlationId;
    
    public enum OrderStatus {
        NEW,           // Order created
        BILLED,        // CMS billing completed
        READY,         // WMS package ready
        ROUTED,        // ROS route planned
        ASSIGNED,      // Driver assigned
        IN_TRANSIT,    // Package in transit
        DELIVERED,     // Delivery completed
        CANCELLED,     // Order cancelled
        FAILED         // Order failed
    }
}


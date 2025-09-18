package com.swiftlogistics.orchestrator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {
    
    @Id
    private String orderId;
    
    private String customerId;

    // Delivery Address
    private String deliveryAddress;
    private String city;
    private String postalCode;
    private String country;
    
    // Order Status Tracking
    @Indexed
    private OrderStatus status;
    
    // Timestamps
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    private LocalDateTime billedAt;
    private LocalDateTime packageReadyAt;
    private LocalDateTime routedAt;
    private LocalDateTime deliveredAt;
    
    // Billing Information
    private Double totalAmount;
    private String billingStatus;
    
    // Correlation ID for tracking across systems
    @Indexed
    private String correlationId;
    
    public enum OrderStatus {
        NEW,           // Order created
        BILLED,        // CMS billing completed
        PACKAGING,     // WMS packaging in progress
        READY,         // WMS package ready
        ROUTED,        // ROS route planned
        ASSIGNED,      // Driver assigned
        IN_TRANSIT,    // Package in transit
        DELIVERED,     // Delivery completed
        CANCELLED,     // Order cancelled
        FAILED         // Order failed
    }
}
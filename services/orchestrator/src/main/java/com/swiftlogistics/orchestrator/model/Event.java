package com.swiftlogistics.orchestrator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "events")
public class Event {
    
    @Id
    private String eventId;
    
    @Indexed
    private String orderId;
    
    @Indexed
    private String correlationId;
    
    private EventType eventType;
    
    private EventSource source;
    
    private String description;
    
    // Event payload/details
    private Map<String, Object> payload;
    
    @CreatedDate
    private LocalDateTime timestamp;
    
    // Success/failure status
    private EventStatus status;
    
    private String errorMessage;
    
    public enum EventType {
        ORDER_CREATED,
        ORDER_SENT_TO_CMS,
        BILLING_COMPLETED,
        BILLING_FAILED,
        ORDER_SENT_TO_WMS,
        PACKAGE_READY,
        WAREHOUSE_FAILED,
        ROUTE_REQUESTED,
        ROUTE_COMPLETED,
        ROUTE_FAILED,
        DRIVER_ASSIGNED,
        DRIVER_UPDATED,
        DELIVERY_STARTED,
        DELIVERY_COMPLETED,
        DELIVERY_FAILED,
        ORDER_CANCELLED
    }
    
    public enum EventSource {
        ORCHESTRATOR,
        CMS_ADAPTER,
        WMS_ADAPTER,
        ROS_ADAPTER,
        DRIVER_APP,
        CLIENT_PORTAL
    }
    
    public enum EventStatus {
        SUCCESS,
        FAILED,
        PENDING,
        WARNING
    }
}
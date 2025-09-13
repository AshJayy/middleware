package com.swiftlogistics.orchestrator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverUpdateMessage {
    private String orderId;
    private String correlationId;
    private String driverId;
    private String driverName;
    private String vehicleId;
    private String status; // "ASSIGNED", "PICKUP_COMPLETE", "IN_TRANSIT", "DELIVERED", "FAILED"
    private String currentLocation;
    private Double latitude;
    private Double longitude;
    private String notes;
    private String errorMessage;
    private LocalDateTime timestamp;
}
package com.swiftlogistics.orchestrator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RouteUpdateMessage {
    private String orderId;
    private String status; // "ROUTED", "FAILED", "OPTIMIZING"
    private List<String> waypoints;
    private String driverId;
    private String driverName;
    private String vehicleId;
    private Double estimatedDistance;
    private Integer estimatedTimeMinutes;
    private String errorMessage;
    private LocalDateTime timestamp;
}
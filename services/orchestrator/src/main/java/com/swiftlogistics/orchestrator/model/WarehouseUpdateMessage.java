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
public class WarehouseUpdateMessage {
    private String orderId;
    private String correlationId;
    private String status; // "PACKAGE_READY", "FAILED", "PROCESSING"
    private String packageId;
    private String warehouseLocation;
    private String errorMessage;
    private LocalDateTime packageReadyAt;
    private LocalDateTime timestamp;
}


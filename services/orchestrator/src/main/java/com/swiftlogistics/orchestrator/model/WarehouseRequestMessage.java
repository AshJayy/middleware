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
public class WarehouseRequestMessage {
    private String orderId;
    private String correlationId;
    private String customerId;
    private String customerName;
    private String deliveryAddress;
    private String city;
    private String postalCode;
    private String country;
    private Double totalAmount;
    private String status; // "BILLED", "READY_FOR_PROCESSING"
    private LocalDateTime timestamp;
}

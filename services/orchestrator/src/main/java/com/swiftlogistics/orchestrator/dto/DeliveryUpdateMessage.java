package com.swiftlogistics.orchestrator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryUpdateMessage {
    private String orderId;
    private String correlationId;
    private String status; // "DELIVERED", "FAILED", "ATTEMPTED"
    private String failureReason;
    private LocalDateTime deliveredAt;
    private LocalDateTime timestamp;
}
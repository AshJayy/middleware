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
public class BillingUpdateMessage {
    private String orderId;
    private String correlationId;
    private String status; // "BILLED", "FAILED", "PENDING"
    private Double billedAmount;
    private String billingTransactionId;
    private String errorMessage;
    private LocalDateTime timestamp;
}
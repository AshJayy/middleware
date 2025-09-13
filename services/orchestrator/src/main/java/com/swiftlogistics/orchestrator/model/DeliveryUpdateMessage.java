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
public class DeliveryUpdateMessage {
    private String orderId;
    private String correlationId;
    private String status; // "DELIVERED", "FAILED", "ATTEMPTED"
    private String deliveredTo;
    private String signatureUrl;
    private String deliveryNotes;
    private Double latitude;
    private Double longitude;
    private String failureReason;
    private LocalDateTime deliveredAt;
    private LocalDateTime timestamp;
}
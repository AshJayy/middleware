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
public class OrderMessage {
    private String orderId;
    private String customerId;
    private String customerName;
    private String customerEmail;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private Double totalAmount;
    private String correlationId;
    private LocalDateTime timestamp;
}


package com.swiftlogistics.orchestrator.dto;

import com.swiftlogistics.orchestrator.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderStatusMessage {
    private String orderId;
    private OrderStatus status;
    private String message;
    private LocalDateTime timestamp;
}
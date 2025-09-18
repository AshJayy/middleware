package com.swiftlogistics.orchestrator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusMessage {
    private String orderId;
    private String status;
    private Object payload;
    private java.time.LocalDateTime timestamp;
}

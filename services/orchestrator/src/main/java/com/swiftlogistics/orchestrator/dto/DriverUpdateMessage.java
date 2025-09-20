package com.swiftlogistics.orchestrator.dto;

import com.swiftlogistics.orchestrator.model.enums.OrderStatus;
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
public class DriverUpdateMessage {
    private String orderId;
    private String driverId;
    private OrderStatus status;
    private String deliveryAddress;
    private String city;
    private String postalCode;
    private String country;
    private List<String> waypoints;
    private LocalDateTime timestamp;
}
package com.swiftlogistics.orchestrator.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    private String customerId;

    // Delivery Address
    private String deliveryAddress;
    private String city;
    private String postalCode;
    private String country;

    // Billing Information
    private Double totalAmount;
}

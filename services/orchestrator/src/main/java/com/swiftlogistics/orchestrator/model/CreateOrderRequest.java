package com.swiftlogistics.orchestrator.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {
    private String customerId;
    private String customerName;
    private String customerEmail;
    private String deliveryAddress;
    private String city;
    private String postalCode;
    private String country;
    private Double totalAmount;
}

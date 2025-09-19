package com.swiftlogistics.cms_adapter.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderMessage {
    private String orderId;
    private String customerId;
    private String customerName;
    private String customerEmail;
    private String deliveryAddress;
    private String city;
    private String postalCode;
    private String country;
    private Double totalAmount;
    private LocalDateTime timestamp;
}

package com.swiftlogistics.cms.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.swiftlogistics.cms.model.Order;
import com.swiftlogistics.cms.model.Order.OrderStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    public Order saveOrder(String orderId, String clientId, BigDecimal amount) {
        // Mock CMS - immediately mark as completed
        LocalDateTime now = LocalDateTime.now();
        Order order = Order.builder()
                .orderId(orderId)
                .customerId(clientId)
                .totalAmount(amount.doubleValue())
                .status(OrderStatus.BILLED)  // Mock: immediately completed billing
                .billingStatus("COMPLETED")
                .createdAt(now)
                .billedAt(now)  // Mock: billing completed immediately
                .build();
        log.info("Mock CMS - Saving completed order: {}", order);
        return order;
    }
}


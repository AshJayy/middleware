package com.swiftlogistics.orchestrator.controller;

import com.swiftlogistics.orchestrator.dto.CreateOrderRequest;
import com.swiftlogistics.orchestrator.model.Order;
import com.swiftlogistics.orchestrator.model.Event;
import com.swiftlogistics.orchestrator.service.OrderService;
import com.swiftlogistics.orchestrator.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;

/**
 * Order Controller - Optimized Event-Driven Flow API
 * Provides REST endpoints for the 6-step orchestration flow
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;
    private final EventService eventService;

    /**
     * Create a new order
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        log.info("Creating new order for customer: {}", request.getCustomerId());

        try {
            Order order = orderService.createOrder(request);

            log.info("Order created successfully: orderId={}, correlationId={}", 
                    order.getOrderId(), order.getCorrelationId());

            return ResponseEntity.ok(order);

        } catch (Exception e) {
            log.error("Failed to create order for customer: {}", request.getCustomerId(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get order by ID
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable String orderId) {
        Optional<Order> order = orderService.getOrder(orderId);
        return order.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get orders by customer ID
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomer(@PathVariable String customerId) {
        List<Order> orders = orderService.getOrdersByCustomer(customerId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get orders by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable String status) {
        try {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            List<Order> orders = orderService.getOrdersByStatus(orderStatus);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get order by correlation ID
     */
    @GetMapping("/correlation/{correlationId}")
    public ResponseEntity<Order> getOrderByCorrelationId(@PathVariable String correlationId) {
        Optional<Order> order = orderService.getOrderByCorrelationId(correlationId);
        return order.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get order events/audit trail
     */
    @GetMapping("/{orderId}/events")
    public ResponseEntity<List<Event>> getOrderEvents(@PathVariable String orderId) {
        try {
            List<Event> events = eventService.getOrderEvents(orderId);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            log.error("Failed to retrieve events for order: {}", orderId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Orchestrator",
            "version", "1.0.0-OPTIMIZED"
        ));
    }
}


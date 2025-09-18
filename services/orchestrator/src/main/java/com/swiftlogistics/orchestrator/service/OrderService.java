package com.swiftlogistics.orchestrator.service;

import com.swiftlogistics.orchestrator.model.Order;
import com.swiftlogistics.orchestrator.model.OrderMessage;
import com.swiftlogistics.orchestrator.model.WarehouseRequestMessage;
import com.swiftlogistics.orchestrator.repository.OrderRepository;
import com.swiftlogistics.orchestrator.messaging.publisher.OrderPublisher;
import com.swiftlogistics.orchestrator.messaging.publisher.WebSocketPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderPublisher orderPublisher;
    private final WebSocketPublisher webSocketPublisher;
    private final EventService eventService;

    /**
     * Step 1: Create a new order and publish to order-created queue
     */
    public Order createOrder(String customerId, String customerName, String customerEmail,
                           String deliveryAddress, String city, String postalCode, String country,
                           Double totalAmount) {
        try {
            // Generate correlation ID for tracking across systems
            String correlationId = UUID.randomUUID().toString();
            
            // Create order entity
            Order order = Order.builder()
                    .customerId(customerId)
                    .deliveryAddress(deliveryAddress)
                    .city(city)
                    .postalCode(postalCode)
                    .country(country)
                    .totalAmount(totalAmount)
                    .status(Order.OrderStatus.NEW)
                    .correlationId(correlationId)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // Save to MongoDB
            Order savedOrder = orderRepository.save(order);
            
            // Log order creation event
            eventService.logEvent(savedOrder.getOrderId(), correlationId, "ORDER_CREATED", 
                    "ORCHESTRATOR", "Order created successfully");

            // Create message for CMS Adapter (billing)
            OrderMessage orderMessage = OrderMessage.builder()
                    .orderId(savedOrder.getOrderId())
                    .customerId(customerId)
                    .customerName(customerName)
                    .customerEmail(customerEmail)
                    .address(deliveryAddress)
                    .city(city)
                    .postalCode(postalCode)
                    .country(country)
                    .totalAmount(totalAmount)
                    .correlationId(correlationId)
                    .timestamp(LocalDateTime.now())
                    .build();

            // Publish to order-created queue for CMS Adapter
            orderPublisher.publishOrderCreated(orderMessage);
            
            // Log order sent to CMS event
            eventService.logEvent(savedOrder.getOrderId(), correlationId, "ORDER_SENT_TO_CMS", 
                    "ORCHESTRATOR", "Order sent to CMS for billing");

            // Publish real-time update via WebSocket
            webSocketPublisher.publishOrderStatusUpdate(savedOrder.getOrderId(), "NEW", savedOrder);

            log.info("Order created successfully: orderId={}, correlationId={}", 
                    savedOrder.getOrderId(), correlationId);

            return savedOrder;

        } catch (Exception e) {
            log.error("Failed to create order: customerId={}", customerId, e);
            throw new RuntimeException("Failed to create order", e);
        }
    }

    /**
     * Update order status (called by billing/warehouse/route subscribers)
     */
    public Order updateOrderStatus(String orderId, String status, Double billedAmount) {
        try {
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isEmpty()) {
                throw new RuntimeException("Order not found: " + orderId);
            }

            Order order = orderOpt.get();
            Order.OrderStatus previousStatus = order.getStatus();
            Order.OrderStatus newStatus = Order.OrderStatus.valueOf(status);
            
            order.setStatus(newStatus);
            order.setUpdatedAt(LocalDateTime.now());

            // Update specific fields based on status
            switch (newStatus) {
                case BILLED:
                    order.setBilledAt(LocalDateTime.now());
                    if (billedAmount != null) {
                        order.setTotalAmount(billedAmount);
                    }
                    break;
                case READY:
                    order.setPackageReadyAt(LocalDateTime.now());
                    break;
                case ROUTED:
                    order.setRoutedAt(LocalDateTime.now());
                    break;
                case DELIVERED:
                    order.setDeliveredAt(LocalDateTime.now());
                    break;
            }

            Order updatedOrder = orderRepository.save(order);
            
            // Log status change event
            eventService.logEvent(orderId, order.getCorrelationId(), "ORDER_STATUS_UPDATED", 
                    "ORCHESTRATOR", String.format("Status changed from %s to %s", previousStatus, newStatus));

            // Publish real-time update via WebSocket
            webSocketPublisher.publishOrderStatusUpdate(orderId, status, updatedOrder);

            log.info("Order status updated: orderId={}, status={}", orderId, status);
            
            return updatedOrder;

        } catch (Exception e) {
            log.error("Failed to update order status: orderId={}, status={}", orderId, status, e);
            throw e;
        }
    }

    /**
     * Step 3: Send order to warehouse after billing completion
     */
    public void sendToWarehouse(String orderId) {
        try {
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isEmpty()) {
                throw new RuntimeException("Order not found: " + orderId);
            }

            Order order = orderOpt.get();
            
            // Create warehouse request message
            WarehouseRequestMessage warehouseRequest = WarehouseRequestMessage.builder()
                    .orderId(orderId)
                    .correlationId(order.getCorrelationId())
                    .customerId(order.getCustomerId())
                    .deliveryAddress(order.getDeliveryAddress())
                    .city(order.getCity())
                    .postalCode(order.getPostalCode())
                    .country(order.getCountry())
                    .totalAmount(order.getTotalAmount())
                    .status("BILLED")
                    .timestamp(LocalDateTime.now())
                    .build();

            // Publish to warehouse queue for WMS Adapter
            orderPublisher.publishToWarehouse(warehouseRequest);
            
            // Log event
            eventService.logEvent(orderId, order.getCorrelationId(), "ORDER_SENT_TO_WMS", 
                    "ORCHESTRATOR", "Order sent to WMS for processing");

            log.info("Order sent to warehouse: orderId={}", orderId);

        } catch (Exception e) {
            log.error("Failed to send order to warehouse: orderId={}", orderId, e);
            eventService.logFailedEvent(orderId, null, "ORDER_SENT_TO_WMS", 
                    "ORCHESTRATOR", "Failed to send order to WMS", e.getMessage());
            throw e;
        }
    }

    /**
     * Step 4: Request route planning after warehouse processing
     */
    public void requestRoutePlanning(String orderId) {
        try {
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isEmpty()) {
                throw new RuntimeException("Order not found: " + orderId);
            }

            Order order = orderOpt.get();
            
            // Create route planning request
            OrderMessage routeRequest = OrderMessage.builder()
                    .orderId(orderId)
                    .customerId(order.getCustomerId())
                    .address(order.getDeliveryAddress())
                    .city(order.getCity())
                    .postalCode(order.getPostalCode())
                    .country(order.getCountry())
                    .correlationId(order.getCorrelationId())
                    .timestamp(LocalDateTime.now())
                    .build();

            // Publish to route planning queue for ROS Adapter
            orderPublisher.publishRoutePlanningRequest(routeRequest);
            
            // Log event
            eventService.logEvent(orderId, order.getCorrelationId(), "ROUTE_REQUESTED", 
                    "ORCHESTRATOR", "Route planning requested from ROS");

            log.info("Route planning requested: orderId={}", orderId);

        } catch (Exception e) {
            log.error("Failed to request route planning: orderId={}", orderId, e);
            eventService.logFailedEvent(orderId, null, "ROUTE_REQUESTED", 
                    "ORCHESTRATOR", "Failed to request route planning", e.getMessage());
            throw e;
        }
    }

    /**
     * Update order with route information
     */
    public void updateOrderRoute(String orderId, List<String> waypoints, String driverId,
                                 String driverName, String vehicleId) {
        try {
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isEmpty()) {
                throw new RuntimeException("Order not found: " + orderId);
            }

            Order order = orderOpt.get();
            order.setStatus(Order.OrderStatus.ROUTED);
            order.setRoutedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());

            Order updatedOrder = orderRepository.save(order);

            // Log event
            eventService.logEvent(orderId, order.getCorrelationId(), "ROUTE_COMPLETED",
                    "ORCHESTRATOR", "Route planning completed and driver assigned");

            // Publish real-time update via WebSocket
            webSocketPublisher.publishOrderStatusUpdate(orderId, "ROUTED", updatedOrder);

            log.info("Order route updated: orderId={}, driverId={}", orderId, driverId);

        } catch (Exception e) {
            log.error("Failed to update order route: orderId={}", orderId, e);
            throw e;
        }
    }

    /**
     * Mark order as delivered
     */
    public void markAsDelivered(String orderId) {
        updateOrderStatus(orderId, "DELIVERED", null);
    }

    /**
     * Get order by ID
     */
    public Optional<Order> getOrder(String orderId) {
        return orderRepository.findById(orderId);
    }

    /**
     * Get orders by customer
     */
    public List<Order> getOrdersByCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    /**
     * Get orders by status
     */
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * Get order by correlation ID
     */
    public Optional<Order> getOrderByCorrelationId(String correlationId) {
        return orderRepository.findByCorrelationId(correlationId);
    }
}
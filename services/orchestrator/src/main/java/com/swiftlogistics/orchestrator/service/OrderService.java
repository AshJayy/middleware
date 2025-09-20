package com.swiftlogistics.orchestrator.service;

import com.swiftlogistics.orchestrator.dto.CreateOrderRequest;
import com.swiftlogistics.orchestrator.dto.RouteRequestMessage;
import com.swiftlogistics.orchestrator.model.Customer;
import com.swiftlogistics.orchestrator.model.Order;
import com.swiftlogistics.orchestrator.dto.OrderMessage;
import com.swiftlogistics.orchestrator.dto.WarehouseRequestMessage;
import com.swiftlogistics.orchestrator.model.enums.EventSource;
import com.swiftlogistics.orchestrator.model.enums.EventType;
import com.swiftlogistics.orchestrator.model.enums.OrderStatus;
import com.swiftlogistics.orchestrator.repository.CustomerRepository;
import com.swiftlogistics.orchestrator.repository.OrderRepository;
import com.swiftlogistics.orchestrator.messaging.publisher.OrderPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderPublisher orderPublisher;
    private final EventService eventService;

    /**
     * Step 1: Create a new order and publish to order-created queue
     */
    public Order createOrder(CreateOrderRequest request) {
        try {
            // Create order entity
            Order order = Order.builder()
                    .customerId(request.getCustomerId())
                    .deliveryAddress(request.getDeliveryAddress())
                    .city(request.getCity())
                    .postalCode(request.getPostalCode())
                    .country(request.getCountry())
                    .totalAmount(request.getTotalAmount())
                    .status(OrderStatus.NEW)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // Save to MongoDB
            Order savedOrder = orderRepository.save(order);
            
            // Log order creation event
            eventService.logSuccessEvent(
                    savedOrder.getOrderId(), EventType.ORDER_CREATED, EventSource.ORCHESTRATOR, "Order created successfully"
            );

            // Get customer details
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow();

            // Create message for CMS Adapter (billing)
            OrderMessage orderMessage = OrderMessage.builder()
                    .orderId(savedOrder.getOrderId())
                    .customerId(savedOrder.getCustomerId())
                    .customerName(customer.getCustomerName())
                    .customerEmail(customer.getCustomerEmail())
                    .deliveryAddress(savedOrder.getDeliveryAddress())
                    .city(savedOrder.getCity())
                    .postalCode(savedOrder.getPostalCode())
                    .country(savedOrder.getCountry())
                    .totalAmount(savedOrder.getTotalAmount())
                    .timestamp(LocalDateTime.now())
                    .build();

            // Publish to order-created queue for CMS Adapter
            orderPublisher.publishOrderCreated(orderMessage);
            
            // Log order sent to CMS event
            eventService.logSuccessEvent(
                    savedOrder.getOrderId(), EventType.ORDER_SENT_TO_CMS, EventSource.ORCHESTRATOR, "Order sent to CMS for billing"
            );

            log.info("Order created successfully: orderId={}",
                    savedOrder.getOrderId());

            return savedOrder;

        } catch (Exception e) {
            log.error("Failed to create order: customerId={}", request.getCustomerId(), e);
            throw new RuntimeException("Failed to create order", e);
        }
    }

    /**
     * Update order status (called by billing/warehouse/route subscribers)
     */
    public void updateOrderStatus(String orderId, OrderStatus status) {
        try {
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isEmpty()) {
                throw new RuntimeException("Order not found: " + orderId);
            }

            Order order = orderOpt.get();
            order.setStatus(status);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);

            log.info("Order status updated: orderId={}, status={}", orderId, status);

        } catch (Exception e) {
            log.error("Failed to update order status: orderId={}", orderId, e);
            throw new RuntimeException("Failed to update order status", e);
        }
    }

    public void updateOrderStatus(String orderId, OrderStatus status, String billingStatus, Double totalAmount) {
        try {
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isEmpty()) {
                throw new RuntimeException("Order not found: " + orderId);
            }

            Order order = orderOpt.get();
            order.setBillingStatus(billingStatus);
            order.setStatus(status);
            order.setTotalAmount(totalAmount);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);

            log.info("Order billing updated: orderId={}, billingStatus={}",
                    orderId, billingStatus);

        } catch (Exception e) {
            log.error("Failed to update order billing: orderId={}", orderId, e);
            throw new RuntimeException("Failed to update order billing", e);
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
            eventService.logSuccessEvent(
                    orderId, EventType.ORDER_SENT_TO_WMS, EventSource.ORCHESTRATOR, "Order sent to WMS for processing"
            );

            log.info("Order sent to warehouse: orderId={}", orderId);

        } catch (Exception e) {
            log.error("Failed to send order to warehouse: orderId={}", orderId, e);
            eventService.logFailedEvent(
                    orderId, EventType.ORDER_SENT_TO_WMS, EventSource.ORCHESTRATOR, "Failed to send order to WMS"
            );
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
            RouteRequestMessage routeRequest = RouteRequestMessage.builder()
                    .orderId(orderId)
                    .deliveryAddress(order.getDeliveryAddress())
                    .city(order.getCity())
                    .postalCode(order.getPostalCode())
                    .country(order.getCountry())
                    .build();

            // Publish to route planning queue for ROS Adapter
            orderPublisher.publishRoutePlanningRequest(routeRequest);
            
            // Log event
            eventService.logSuccessEvent(
                    orderId, EventType.ORDER_SENT_TO_ROS, EventSource.ORCHESTRATOR, "Route planning requested"
            );

            log.info("Route planning requested: orderId={}", orderId);

        } catch (Exception e) {
            log.error("Failed to request route planning: orderId={}", orderId, e);
            eventService.logFailedEvent(
                    orderId, EventType.ORDER_SENT_TO_ROS, EventSource.ORCHESTRATOR, "Failed to request route planning"
            );
            throw e;
        }
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

}
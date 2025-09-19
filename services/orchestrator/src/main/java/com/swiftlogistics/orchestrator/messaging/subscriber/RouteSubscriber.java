package com.swiftlogistics.orchestrator.messaging.subscriber;

import com.swiftlogistics.orchestrator.config.RabbitMQConfig;
import com.swiftlogistics.orchestrator.dto.RouteUpdateMessage;
import com.swiftlogistics.orchestrator.messaging.publisher.SsePublisher;
import com.swiftlogistics.orchestrator.model.Route;
import com.swiftlogistics.orchestrator.model.enums.EventSource;
import com.swiftlogistics.orchestrator.model.enums.EventType;
import com.swiftlogistics.orchestrator.model.enums.OrderStatus;
import com.swiftlogistics.orchestrator.service.OrderService;
import com.swiftlogistics.orchestrator.service.EventService;
import com.swiftlogistics.orchestrator.service.RouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

/**
 * Route Subscriber - Step 4 of Optimized Event-Driven Flow
 * Handles route planning updates and driver assignment preparation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RouteSubscriber {

    private final OrderService orderService;
    private final RouteService routeService;
    private final EventService eventService;
    private final SsePublisher ssePublisher;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ROUTE_UPDATES)
    @Retryable(
        value = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000)
    )
    public void processRouteUpdate(RouteUpdateMessage routeUpdate) throws Exception {
        try {
            log.info("Received route update: orderId={}, status={}",
                    routeUpdate.getOrderId(), routeUpdate.getStatus());

            // Process based on route status
            switch (routeUpdate.getStatus().toUpperCase()) {
                case "ROUTED" -> handleRouted(routeUpdate);
                case "FAILED" -> handleRouteFailure(routeUpdate);
                case "ROUTING" -> handleRouting(routeUpdate);
                default -> log.warn("Unknown route status: {} for order: {}",
                            routeUpdate.getStatus(), routeUpdate.getOrderId());
            }

            log.info("Route update processed successfully for order: {}", routeUpdate.getOrderId());

        } catch (Exception e) {
            log.error("Error processing route update for order: {}", routeUpdate.getOrderId(), e);
            
            eventService.logFailedEvent(
                routeUpdate.getOrderId(),
                    EventType.ROUTE_FAILED,
                    EventSource.ROS_ADAPTER,
                "Failed to process route update: " + e.getMessage()
            );
        }
    }

    private void handleRouted(RouteUpdateMessage routeUpdate) {
        // Update order status to ROUTED
        orderService.updateOrderStatus(routeUpdate.getOrderId(), OrderStatus.ROUTED);

        // Create route in the system
        Route route = routeService.createOrderRoute(
                routeUpdate.getOrderId(),
                routeUpdate.getWaypoints(),
                routeUpdate.getDriverId()
        );

        // Log success event
        eventService.logSuccessEvent(
                routeUpdate.getOrderId(),
                EventType.ROUTE_CREATED,
                EventSource.ROS_ADAPTER,
                "Route created with driverId: " + routeUpdate.getDriverId()
        );

        // Notify clients via SSE
        ssePublisher.publishOrderStatusUpdate(
                routeUpdate.getOrderId(), OrderStatus.ROUTED, "Route created successfully"
        );

//        ssePublisher.publishDriverUpdate(route.getRouteId());

        log.info("Order routed successfully: orderId={}, driverId={}",
                routeUpdate.getOrderId(), routeUpdate.getDriverId());
    }

    private void handleRouteFailure(RouteUpdateMessage routeUpdate) {
        // Update order status to ROUTE_FAILED
        orderService.updateOrderStatus(routeUpdate.getOrderId(), OrderStatus.ROUTE_FAILED);

        // Log failure event
        eventService.logFailedEvent(
                routeUpdate.getOrderId(),
                EventType.ROUTE_FAILED,
                EventSource.ROS_ADAPTER,
                "Route planning failed"
        );

        // Notify clients via SSE
        ssePublisher.publishOrderStatusUpdate(
                routeUpdate.getOrderId(), OrderStatus.ROUTE_FAILED, "Route planning failed"
        );

        log.error("Route planning failed for order: {}", routeUpdate.getOrderId());
    }

    private void handleRouting(RouteUpdateMessage routeUpdate) {
        // Update order status to ROUTING (optional, depending on business logic)
        orderService.updateOrderStatus(routeUpdate.getOrderId(), OrderStatus.ROUTING);

        // Log pending event but don't change order status yet
        eventService.logPendingEvent(
                routeUpdate.getOrderId(),
                EventType.ROUTE_PENDING,
                EventSource.ROS_ADAPTER,
                "Route planning is in progress"
        );

        // Notify clients via SSE
        ssePublisher.publishOrderStatusUpdate(
                routeUpdate.getOrderId(), OrderStatus.ROUTING, "Route planning is in progress"
        );

        log.info("Route planning in progress for order: {}", routeUpdate.getOrderId());
    }
}

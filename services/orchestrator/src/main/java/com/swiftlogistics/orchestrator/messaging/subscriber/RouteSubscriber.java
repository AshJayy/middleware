package com.swiftlogistics.orchestrator.messaging.subscriber;

import com.swiftlogistics.orchestrator.config.RabbitMQConfig;
import com.swiftlogistics.orchestrator.dto.RouteUpdateMessage;
import com.swiftlogistics.orchestrator.service.OrderService;
import com.swiftlogistics.orchestrator.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
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
    private final EventService eventService;
    private final SimpMessagingTemplate messagingTemplate;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ROUTE_UPDATES)
    @Retryable(
        value = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000)
    )
    public void processRouteUpdate(RouteUpdateMessage routeUpdate) throws Exception {
        log.info("Processing route update for order: {} with status: {}", 
                routeUpdate.getOrderId(), routeUpdate.getStatus());

        try {
            // Log the event
            eventService.logEvent(
                routeUpdate.getOrderId(),
                routeUpdate.getCorrelationId(),
                "ROUTE_UPDATE_RECEIVED",
                "ROUTE_SERVICE",
                "Route update: " + routeUpdate.getStatus()
            );

            // Process based on route status
            switch (routeUpdate.getStatus().toUpperCase()) {
                case "ROUTED":
                    // Route is complete with driver assignment
                    orderService.updateOrderRoute(
                        routeUpdate.getOrderId(),
                        routeUpdate.getWaypoints(),
                        routeUpdate.getDriverId(),
                        routeUpdate.getDriverName(),
                        routeUpdate.getVehicleId()
                    );
                    break;
                    
                case "OPTIMIZING":
                    // Route is being optimized - just log for now
                    eventService.logEvent(
                        routeUpdate.getOrderId(),
                        routeUpdate.getCorrelationId(),
                        "ROUTE_OPTIMIZING",
                        "ROUTE_SERVICE",
                        "Route optimization in progress"
                    );
                    break;
                    
                case "FAILED":
                    eventService.logFailedEvent(
                        routeUpdate.getOrderId(),
                        routeUpdate.getCorrelationId(),
                        "ROUTE_PLANNING_FAILED",
                        "ROUTE_SERVICE",
                        "Route planning failed",
                        routeUpdate.getErrorMessage()
                    );
                    throw new Exception("Route planning failed: " + routeUpdate.getErrorMessage());
                    
                default:
                    log.warn("Unknown route status: {} for order: {}", 
                            routeUpdate.getStatus(), routeUpdate.getOrderId());
            }

            // Notify WebSocket clients
            messagingTemplate.convertAndSend(
                "/topic/orders/" + routeUpdate.getOrderId() + "/route-updates",
                routeUpdate
            );

            log.info("Route update processed successfully for order: {}", routeUpdate.getOrderId());

        } catch (Exception e) {
            log.error("Error processing route update for order: {}", routeUpdate.getOrderId(), e);
            
            eventService.logFailedEvent(
                routeUpdate.getOrderId(),
                routeUpdate.getCorrelationId(),
                "ROUTE_UPDATE_PROCESSING_FAILED",
                "ORCHESTRATOR",
                "Failed to process route update",
                e.getMessage()
            );
            
            throw e;
        }
    }

    @Recover
    public void recover(Exception e, RouteUpdateMessage routeUpdate) {
        log.error("Route update processing failed after retries for order: {}. Sending to DLQ", 
                routeUpdate.getOrderId(), e);
        
        // Log final failure
        eventService.logFailedEvent(
            routeUpdate.getOrderId(),
            routeUpdate.getCorrelationId(),
            "ROUTE_UPDATE_FINAL_FAILURE",
            "ORCHESTRATOR",
            "Route update processing failed after all retries",
            e.getMessage()
        );
        
        // Send to DLQ
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_ROUTE + RabbitMQConfig.DLX_SUFFIX,
            RabbitMQConfig.QUEUE_ROUTE_UPDATES + RabbitMQConfig.DLQ_SUFFIX,
            routeUpdate
        );
    }
}

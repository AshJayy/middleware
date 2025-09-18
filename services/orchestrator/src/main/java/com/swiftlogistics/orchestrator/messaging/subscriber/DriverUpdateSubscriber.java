package com.swiftlogistics.orchestrator.messaging.subscriber;

import com.swiftlogistics.orchestrator.config.RabbitMQConfig;
import com.swiftlogistics.orchestrator.dto.DriverUpdateMessage;
import com.swiftlogistics.orchestrator.service.OrderService;
import com.swiftlogistics.orchestrator.service.EventService;
import com.swiftlogistics.orchestrator.messaging.publisher.WebSocketPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

/**
 * Driver Update Subscriber - Step 5 of Optimized Event-Driven Flow
 * Handles driver assignment updates and delivery initiation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DriverUpdateSubscriber {

    private final OrderService orderService;
    private final EventService eventService;
    private final WebSocketPublisher webSocketPublisher;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_DRIVER_UPDATES)
    @Retryable(
        value = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000)
    )
    public void processDriverUpdate(DriverUpdateMessage driverUpdate) throws Exception {
        log.info("Processing driver update for order: {} with status: {}", 
                driverUpdate.getOrderId(), driverUpdate.getStatus());

        try {
            // Log the event
            eventService.logEvent(
                driverUpdate.getOrderId(),
                driverUpdate.getCorrelationId(),
                "DRIVER_UPDATE_RECEIVED",
                "DRIVER_SERVICE",
                "Driver update: " + driverUpdate.getStatus()
            );

            // Process based on driver status
            switch (driverUpdate.getStatus().toUpperCase()) {
                case "ASSIGNED":
                    handleDriverAssignment(driverUpdate);
                    break;
                    
                case "EN_ROUTE":
                    handleDriverEnRoute(driverUpdate);
                    break;
                    
                case "PICKUP_COMPLETED":
                    handlePickupCompleted(driverUpdate);
                    break;
                    
                case "FAILED":
                    handleDriverFailure(driverUpdate);
                    break;
                    
                default:
                    log.warn("Unknown driver status: {} for order: {}", 
                            driverUpdate.getStatus(), driverUpdate.getOrderId());
            }

            // Notify WebSocket clients for both order tracking and driver app
            webSocketPublisher.publishDriverUpdate(driverUpdate);

            log.info("Driver update processed successfully for order: {}", driverUpdate.getOrderId());

        } catch (Exception e) {
            log.error("Error processing driver update for order: {}", driverUpdate.getOrderId(), e);
            
            eventService.logFailedEvent(
                driverUpdate.getOrderId(),
                driverUpdate.getCorrelationId(),
                "DRIVER_UPDATE_PROCESSING_FAILED",
                "ORCHESTRATOR",
                "Failed to process driver update",
                e.getMessage()
            );
            
            throw e;
        }
    }

    private void handleDriverAssignment(DriverUpdateMessage driverUpdate) {
        // Update order with driver details
        orderService.updateOrderRoute(
            driverUpdate.getOrderId(),
            null, // Keep existing waypoints
            driverUpdate.getDriverId(),
            driverUpdate.getDriverName(),
            driverUpdate.getVehicleId()
        );

        eventService.logEvent(
            driverUpdate.getOrderId(),
            driverUpdate.getCorrelationId(),
            "DRIVER_ASSIGNED",
            "DRIVER_SERVICE",
            String.format("Driver %s assigned with vehicle %s", 
                    driverUpdate.getDriverName(), driverUpdate.getVehicleId())
        );
    }

    private void handleDriverEnRoute(DriverUpdateMessage driverUpdate) {
        eventService.logEvent(
            driverUpdate.getOrderId(),
            driverUpdate.getCorrelationId(),
            "DRIVER_EN_ROUTE",
            "DRIVER_SERVICE",
            "Driver is en route to pickup location"
        );
    }

    private void handlePickupCompleted(DriverUpdateMessage driverUpdate) {
        // Update order status - driver has picked up the package
        orderService.updateOrderStatus(driverUpdate.getOrderId(), "IN_TRANSIT", null);
        
        eventService.logEvent(
            driverUpdate.getOrderId(),
            driverUpdate.getCorrelationId(),
            "PACKAGE_PICKED_UP",
            "DRIVER_SERVICE",
            "Package picked up and delivery started"
        );
    }

    private void handleDriverFailure(DriverUpdateMessage driverUpdate) {
        eventService.logFailedEvent(
            driverUpdate.getOrderId(),
            driverUpdate.getCorrelationId(),
            "DRIVER_ASSIGNMENT_FAILED",
            "DRIVER_SERVICE",
            "Driver assignment failed",
            driverUpdate.getErrorMessage()
        );
        
        throw new RuntimeException("Driver assignment failed: " + driverUpdate.getErrorMessage());
    }

    @Recover
    public void recover(Exception e, DriverUpdateMessage driverUpdate) {
        log.error("Driver update processing failed after retries for order: {}. Sending to DLQ", 
                driverUpdate.getOrderId(), e);
        
        // Log final failure
        eventService.logFailedEvent(
            driverUpdate.getOrderId(),
            driverUpdate.getCorrelationId(),
            "DRIVER_UPDATE_FINAL_FAILURE",
            "ORCHESTRATOR",
            "Driver update processing failed after all retries",
            e.getMessage()
        );
        
        // Send to DLQ
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_DRIVER + RabbitMQConfig.DLX_SUFFIX,
            RabbitMQConfig.QUEUE_DRIVER_UPDATES + RabbitMQConfig.DLQ_SUFFIX,
            driverUpdate
        );
    }
}
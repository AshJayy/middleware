package com.swiftlogistics.orchestrator.messaging.subscriber;

import com.swiftlogistics.orchestrator.config.RabbitMQConfig;
import com.swiftlogistics.orchestrator.dto.DeliveryUpdateMessage;
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
 * Delivery Update Subscriber - Step 6 of Optimized Event-Driven Flow
 * Handles final delivery updates and order completion
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryUpdateSubscriber {

    private final OrderService orderService;
    private final EventService eventService;
    private final SimpMessagingTemplate messagingTemplate;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_DELIVERY_UPDATES)
    @Retryable(
        value = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000)
    )
    public void processDeliveryUpdate(DeliveryUpdateMessage deliveryUpdate) throws Exception {
        log.info("Processing delivery update for order: {} with status: {}", 
                deliveryUpdate.getOrderId(), deliveryUpdate.getStatus());

        try {
            // Log the event
            eventService.logEvent(
                deliveryUpdate.getOrderId(),
                deliveryUpdate.getCorrelationId(),
                "DELIVERY_UPDATE_RECEIVED",
                "DELIVERY_SERVICE",
                "Delivery update: " + deliveryUpdate.getStatus()
            );

            // Process based on delivery status
            switch (deliveryUpdate.getStatus().toUpperCase()) {
                case "DELIVERED":
                    handleDeliveryCompleted(deliveryUpdate);
                    break;
                    
                case "IN_TRANSIT":
                    handleInTransit(deliveryUpdate);
                    break;
                    
                case "ATTEMPTED":
                    handleDeliveryAttempted(deliveryUpdate);
                    break;
                    
                case "FAILED":
                    handleDeliveryFailed(deliveryUpdate);
                    break;
                    
                default:
                    log.warn("Unknown delivery status: {} for order: {}", 
                            deliveryUpdate.getStatus(), deliveryUpdate.getOrderId());
            }

            // Notify WebSocket clients
            messagingTemplate.convertAndSend(
                "/topic/orders/" + deliveryUpdate.getOrderId() + "/delivery-updates",
                deliveryUpdate
            );

            log.info("Delivery update processed successfully for order: {}", deliveryUpdate.getOrderId());

        } catch (Exception e) {
            log.error("Error processing delivery update for order: {}", deliveryUpdate.getOrderId(), e);
            
            eventService.logFailedEvent(
                deliveryUpdate.getOrderId(),
                deliveryUpdate.getCorrelationId(),
                "DELIVERY_UPDATE_PROCESSING_FAILED",
                "ORCHESTRATOR",
                "Failed to process delivery update",
                e.getMessage()
            );
            
            throw e;
        }
    }

    private void handleDeliveryCompleted(DeliveryUpdateMessage deliveryUpdate) {
        // Mark order as delivered - final step in the flow
        orderService.markAsDelivered(deliveryUpdate.getOrderId());

        eventService.logEvent(
            deliveryUpdate.getOrderId(),
            deliveryUpdate.getCorrelationId(),
            "ORDER_DELIVERED",
            "DELIVERY_SERVICE",
                null
        );

        log.info("Order {} delivery completed successfully", deliveryUpdate.getOrderId());
    }

    private void handleInTransit(DeliveryUpdateMessage deliveryUpdate) {
        eventService.logEvent(
            deliveryUpdate.getOrderId(),
            deliveryUpdate.getCorrelationId(),
            "DELIVERY_IN_TRANSIT",
            "DELIVERY_SERVICE",
                null
        );
    }

    private void handleDeliveryAttempted(DeliveryUpdateMessage deliveryUpdate) {
        eventService.logEvent(
            deliveryUpdate.getOrderId(),
            deliveryUpdate.getCorrelationId(),
            "DELIVERY_ATTEMPTED",
            "DELIVERY_SERVICE",
                null
        );
    }

    private void handleDeliveryFailed(DeliveryUpdateMessage deliveryUpdate) {
        eventService.logFailedEvent(
            deliveryUpdate.getOrderId(),
            deliveryUpdate.getCorrelationId(),
            "DELIVERY_FAILED",
            "DELIVERY_SERVICE",
            "Delivery failed",
            deliveryUpdate.getFailureReason()
        );
        
        // Don't throw exception for delivery failures - they should be handled differently
        log.warn("Delivery failed for order {}: {}", 
                deliveryUpdate.getOrderId(), deliveryUpdate.getFailureReason());
    }

    @Recover
    public void recover(Exception e, DeliveryUpdateMessage deliveryUpdate) {
        log.error("Delivery update processing failed after retries for order: {}. Sending to DLQ", 
                deliveryUpdate.getOrderId(), e);
        
        // Log final failure
        eventService.logFailedEvent(
            deliveryUpdate.getOrderId(),
            deliveryUpdate.getCorrelationId(),
            "DELIVERY_UPDATE_FINAL_FAILURE",
            "ORCHESTRATOR",
            "Delivery update processing failed after all retries",
            e.getMessage()
        );
        
        // Send to DLQ
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_DELIVERY + RabbitMQConfig.DLX_SUFFIX,
            RabbitMQConfig.QUEUE_DELIVERY_UPDATES + RabbitMQConfig.DLQ_SUFFIX,
            deliveryUpdate
        );
    }
}
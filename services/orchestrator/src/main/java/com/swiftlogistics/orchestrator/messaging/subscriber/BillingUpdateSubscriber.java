package com.swiftlogistics.orchestrator.messaging.subscriber;

import com.swiftlogistics.orchestrator.config.RabbitMQConfig;
import com.swiftlogistics.orchestrator.dto.BillingUpdateMessage;
import com.swiftlogistics.orchestrator.messaging.publisher.SsePublisher;
import com.swiftlogistics.orchestrator.model.enums.EventSource;
import com.swiftlogistics.orchestrator.model.enums.EventType;
import com.swiftlogistics.orchestrator.model.enums.OrderStatus;
import com.swiftlogistics.orchestrator.service.OrderService;
import com.swiftlogistics.orchestrator.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingUpdateSubscriber {

    private final OrderService orderService;
    private final EventService eventService;
    private final SsePublisher ssePublisher;

    /**
     * Step 2: Listen for billing updates from CMS Adapter
     * When billing is completed, update order status and trigger warehouse processing
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_BILLING_UPDATES)
    public void handleBillingUpdate(BillingUpdateMessage billingUpdate) {
        try {
            log.info("Received billing update: orderId={}, status={}",
                    billingUpdate.getOrderId(), billingUpdate.getStatus());

            // Process different billing statuses
            switch (billingUpdate.getStatus()) {
                case "BILLED" -> handleBillingSuccess(billingUpdate);
                case "FAILED" -> handleBillingFailure(billingUpdate);
                case "PENDING" -> handleBillingPending(billingUpdate);
                default -> log.warn("Unknown billing status: {} for orderId: {}", 
                        billingUpdate.getStatus(), billingUpdate.getOrderId());
            }

        } catch (Exception e) {
            log.error("Error processing billing update: orderId={}",
                    billingUpdate.getOrderId(), e);
            
            // Log failure event
            eventService.logFailedEvent(
                    billingUpdate.getOrderId(), EventType.BILLING_FAILED,
                    EventSource.ORCHESTRATOR, "Failed to process billing update: " + e.getMessage()
            );
        }
    }

    private void handleBillingSuccess(BillingUpdateMessage billingUpdate) {
        // Update order status to BILLED
        orderService.updateOrderStatus(billingUpdate.getOrderId(), OrderStatus.BILLED, "COMPLETED", billingUpdate.getBilledAmount());
        
        // Log billing completed event
        eventService.logSuccessEvent(
                billingUpdate.getOrderId(), EventType.BILLING_COMPLETED,
                EventSource.CMS_ADAPTER, "Billing completed successfully"
        );

        // Notify clients via SSE
        ssePublisher.publishOrderStatusUpdate(
                billingUpdate.getOrderId(), OrderStatus.BILLED, "Billing completed successfully"
        );
        
        // Trigger warehouse processing
        orderService.sendToWarehouse(billingUpdate.getOrderId());
        
        log.info("Billing completed successfully for orderId: {}", billingUpdate.getOrderId());
    }

    private void handleBillingFailure(BillingUpdateMessage billingUpdate) {
        // Update order status to FAILED
        orderService.updateOrderStatus(billingUpdate.getOrderId(), OrderStatus.BILLING_FAILED,"FAILED", null);
        
        // Log billing failure event
        eventService.logFailedEvent(
                billingUpdate.getOrderId(), EventType.BILLING_FAILED,
                EventSource.CMS_ADAPTER, "Billing failed: "
        );

        // Notify clients via SSE
        ssePublisher.publishOrderStatusUpdate(
                billingUpdate.getOrderId(), OrderStatus.BILLING_FAILED, "Billing failed: "
        );
        
        log.error("Billing failed for orderId: {}, reason: {}", 
                billingUpdate.getOrderId(), "Billing failed in CMS");
    }

    private void handleBillingPending(BillingUpdateMessage billingUpdate) {
        // Log pending status but don't change order status yet
        eventService.logPendingEvent(
                billingUpdate.getOrderId(), EventType.BILLING_PENDING,
                EventSource.CMS_ADAPTER, "Billing is pending"
        );

        // Notify clients via SSE
        ssePublisher.publishOrderStatusUpdate(
                billingUpdate.getOrderId(), OrderStatus.BILLING_PENDING, "Billing is pending"
        );

        log.info("Billing pending for orderId: {}", billingUpdate.getOrderId());
    }
}
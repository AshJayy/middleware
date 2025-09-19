package com.swiftlogistics.orchestrator.messaging.subscriber;

import com.swiftlogistics.orchestrator.config.RabbitMQConfig;
import com.swiftlogistics.orchestrator.model.BillingUpdateMessage;
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

    /**
     * Step 2: Listen for billing updates from CMS Adapter
     * When billing is completed, update order status and trigger warehouse processing
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_BILLING_UPDATES)
    public void handleBillingUpdate(BillingUpdateMessage billingUpdate) {
        try {
            log.info("Received billing update: orderId={}, status={}, correlationId={}", 
                    billingUpdate.getOrderId(), billingUpdate.getStatus(), billingUpdate.getCorrelationId());

            // Process different billing statuses
            switch (billingUpdate.getStatus()) {
                case "BILLED" -> handleBillingSuccess(billingUpdate);
                case "FAILED" -> handleBillingFailure(billingUpdate);
                case "PENDING" -> handleBillingPending(billingUpdate);
                default -> log.warn("Unknown billing status: {} for orderId: {}", 
                        billingUpdate.getStatus(), billingUpdate.getOrderId());
            }

        } catch (Exception e) {
            log.error("Error processing billing update: orderId={}, correlationId={}", 
                    billingUpdate.getOrderId(), billingUpdate.getCorrelationId(), e);
            
            // Log failure event
            eventService.logEvent(billingUpdate.getOrderId(), billingUpdate.getCorrelationId(),
                    "BILLING_UPDATE_PROCESSING_FAILED", "ORCHESTRATOR", "Failed to process billing update: " + e.getMessage());
        }
    }

    private void handleBillingSuccess(BillingUpdateMessage billingUpdate) {
        // Update order status to BILLED
        orderService.updateOrderStatus(billingUpdate.getOrderId(), "BILLED", billingUpdate.getBilledAmount());
        
        // Log billing completed event
        eventService.logEvent(billingUpdate.getOrderId(), billingUpdate.getCorrelationId(),
                "BILLING_COMPLETED", "CMS_ADAPTER", "Order successfully billed");
        
        // Trigger warehouse processing
        orderService.sendToWarehouse(billingUpdate.getOrderId());
        
        log.info("Billing completed successfully for orderId: {}", billingUpdate.getOrderId());
    }

    private void handleBillingFailure(BillingUpdateMessage billingUpdate) {
        // Update order status to FAILED
        orderService.updateOrderStatus(billingUpdate.getOrderId(), "FAILED", null);
        
        // Log billing failure event
        eventService.logEvent(billingUpdate.getOrderId(), billingUpdate.getCorrelationId(),
                "BILLING_FAILED", "CMS_ADAPTER", "Billing failed: " + billingUpdate.getErrorMessage());
        
        log.error("Billing failed for orderId: {}, reason: {}", 
                billingUpdate.getOrderId(), billingUpdate.getErrorMessage());
    }

    private void handleBillingPending(BillingUpdateMessage billingUpdate) {
        // Log pending status but don't change order status yet
        eventService.logEvent(billingUpdate.getOrderId(), billingUpdate.getCorrelationId(),
                "BILLING_PENDING", "CMS_ADAPTER", "Billing is pending");
        
        log.info("Billing pending for orderId: {}", billingUpdate.getOrderId());
    }
}
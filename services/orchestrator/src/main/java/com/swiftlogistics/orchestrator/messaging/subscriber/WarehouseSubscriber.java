package com.swiftlogistics.orchestrator.messaging.subscriber;

import com.swiftlogistics.orchestrator.config.RabbitMQConfig;
import com.swiftlogistics.orchestrator.dto.WarehouseUpdateMessage;
import com.swiftlogistics.orchestrator.service.OrderService;
import com.swiftlogistics.orchestrator.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Step 3: Warehouse Update Subscriber
 * Handles updates from WMS Adapter when packages are ready
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseSubscriber {

    private final OrderService orderService;
    private final EventService eventService;
    private final SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_WAREHOUSE_UPDATES)
    public void handleWarehouseUpdate(WarehouseUpdateMessage warehouseUpdate) {
        try {
            log.info("Received warehouse update: orderId={}, status={}, correlationId={}", 
                    warehouseUpdate.getOrderId(), warehouseUpdate.getStatus(), warehouseUpdate.getCorrelationId());

            // Process different warehouse statuses
            switch (warehouseUpdate.getStatus()) {
                case "PACKAGE_READY" -> handlePackageReady(warehouseUpdate);
                case "FAILED" -> handleWarehouseFailure(warehouseUpdate);
                case "PROCESSING" -> handleWarehouseProcessing(warehouseUpdate);
                default -> log.warn("Unknown warehouse status: {} for orderId: {}", 
                        warehouseUpdate.getStatus(), warehouseUpdate.getOrderId());
            }

            // Push real-time update to WebSocket clients
            messagingTemplate.convertAndSend(
                    "/topic/orders/" + warehouseUpdate.getOrderId() + "/warehouse",
                    warehouseUpdate
            );

        } catch (Exception e) {
            log.error("Error processing warehouse update: orderId={}, correlationId={}", 
                    warehouseUpdate.getOrderId(), warehouseUpdate.getCorrelationId(), e);
            
            // Log failure event
            eventService.logFailedEvent(warehouseUpdate.getOrderId(), warehouseUpdate.getCorrelationId(),
                    "WAREHOUSE_UPDATE_PROCESSING_FAILED", "ORCHESTRATOR", 
                    "Failed to process warehouse update", e.getMessage());
        }
    }

    private void handlePackageReady(WarehouseUpdateMessage warehouseUpdate) {
        // Update order status to READY
        orderService.updateOrderStatus(warehouseUpdate.getOrderId(), "READY", null);
        
        // Log package ready event
        eventService.logEvent(warehouseUpdate.getOrderId(), warehouseUpdate.getCorrelationId(),
                "PACKAGE_READY", "WMS_ADAPTER", 
                "Package ready at warehouse: " + warehouseUpdate.getWarehouseLocation());
        
        // Trigger route planning
        orderService.requestRoutePlanning(warehouseUpdate.getOrderId());
        
        log.info("Package ready for orderId: {}, triggering route planning", warehouseUpdate.getOrderId());
    }

    private void handleWarehouseFailure(WarehouseUpdateMessage warehouseUpdate) {
        // Update order status to FAILED
        orderService.updateOrderStatus(warehouseUpdate.getOrderId(), "FAILED", null);
        
        // Log warehouse failure event
        eventService.logFailedEvent(warehouseUpdate.getOrderId(), warehouseUpdate.getCorrelationId(),
                "WAREHOUSE_FAILED", "WMS_ADAPTER", 
                "Warehouse processing failed", warehouseUpdate.getErrorMessage());
        
        log.error("Warehouse processing failed for orderId: {}, reason: {}", 
                warehouseUpdate.getOrderId(), warehouseUpdate.getErrorMessage());
    }

    private void handleWarehouseProcessing(WarehouseUpdateMessage warehouseUpdate) {
        // Log processing status but don't change order status yet
        eventService.logEvent(warehouseUpdate.getOrderId(), warehouseUpdate.getCorrelationId(),
                "WAREHOUSE_PROCESSING", "WMS_ADAPTER", "Package is being processed at warehouse");
        
        log.info("Warehouse processing for orderId: {}", warehouseUpdate.getOrderId());
    }
}

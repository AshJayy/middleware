package com.swiftlogistics.orchestrator.messaging.subscriber;

import com.swiftlogistics.orchestrator.config.RabbitMQConfig;
import com.swiftlogistics.orchestrator.dto.WarehouseUpdateMessage;
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
    private final SsePublisher ssePublisher;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_WAREHOUSE_UPDATES)
    public void handleWarehouseUpdate(WarehouseUpdateMessage warehouseUpdate) {
        try {
            log.info("Received warehouse update: orderId={}, status={}",
                    warehouseUpdate.getOrderId(), warehouseUpdate.getStatus());

            // Process different warehouse statuses
            switch (warehouseUpdate.getStatus()) {
                case "READY" -> handlePackageReady(warehouseUpdate);
                case "FAILED" -> handleWarehouseFailure(warehouseUpdate);
                case "PROCESSING" -> handleWarehouseProcessing(warehouseUpdate);
                default -> log.warn("Unknown warehouse status: {} for orderId: {}", 
                        warehouseUpdate.getStatus(), warehouseUpdate.getOrderId());
            }

        } catch (Exception e) {
            log.error("Error processing warehouse update: orderId={}",
                    warehouseUpdate.getOrderId(), e);
            
            // Log failure event
            eventService.logFailedEvent(warehouseUpdate.getOrderId(),
                    EventType.WAREHOUSE_FAILED, EventSource.WMS_ADAPTER,
                    "Failed to process warehouse update: " + e.getMessage());
        }
    }

    private void handlePackageReady(WarehouseUpdateMessage warehouseUpdate) {
        // Update order status to READY
        orderService.updateOrderStatus(warehouseUpdate.getOrderId(), OrderStatus.READY);
        
        // Log package ready event
        eventService.logSuccessEvent(warehouseUpdate.getOrderId(), EventType.PACKAGE_READY,
                EventSource.WMS_ADAPTER, "Package is ready for shipment");

        // Notify clients via SSE
        ssePublisher.publishOrderStatusUpdate(
                warehouseUpdate.getOrderId(), OrderStatus.READY, "Package is ready for shipment"
        );

        // Trigger route planning
        orderService.requestRoutePlanning(warehouseUpdate.getOrderId());
        
        log.info("Package ready for orderId: {}, triggering route planning", warehouseUpdate.getOrderId());
    }

    private void handleWarehouseFailure(WarehouseUpdateMessage warehouseUpdate) {
        // Update order status to WAREHOUSE_FAILED
        orderService.updateOrderStatus(warehouseUpdate.getOrderId(), OrderStatus.WAREHOUSE_FAILED);

        // Log warehouse failure event
        eventService.logFailedEvent(warehouseUpdate.getOrderId(),
                EventType.WAREHOUSE_FAILED, EventSource.WMS_ADAPTER,
                "Warehouse processing failed: ");

        // Notify clients via SSE
        ssePublisher.publishOrderStatusUpdate(
                warehouseUpdate.getOrderId(), OrderStatus.WAREHOUSE_FAILED, "Warehouse processing failed"
        );
        
        log.error("Warehouse processing failed for orderId: {}, reason: {}", 
                warehouseUpdate.getOrderId(), null);
    }

    private void handleWarehouseProcessing(WarehouseUpdateMessage warehouseUpdate) {
        // Update order status to PROCESSING (optional, depending on business logic)
         orderService.updateOrderStatus(warehouseUpdate.getOrderId(), OrderStatus.PROCESSING);

        // Log processing status but don't change order status yet
        eventService.logPendingEvent(
                warehouseUpdate.getOrderId(), EventType.WAREHOUSE_PROCESSING,
                EventSource.WMS_ADAPTER, "Warehouse processing in progress"
        );

        // Notify clients via SSE
        ssePublisher.publishOrderStatusUpdate(
                warehouseUpdate.getOrderId(), OrderStatus.PROCESSING, "Warehouse processing in progress"
        );
        
        log.info("Warehouse processing for orderId: {}", warehouseUpdate.getOrderId());
    }
}

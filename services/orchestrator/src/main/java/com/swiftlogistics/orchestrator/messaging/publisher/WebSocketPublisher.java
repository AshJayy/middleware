package com.swiftlogistics.orchestrator.messaging.publisher;

import com.swiftlogistics.orchestrator.model.DriverUpdateMessage;
import com.swiftlogistics.orchestrator.model.OrderStatusMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Publish driver updates to WebSocket for driver app
     */
    public void publishDriverUpdate(DriverUpdateMessage driverUpdate) {
        try {
            String destination = "/topic/driver/" + driverUpdate.getDriverId();
            messagingTemplate.convertAndSend(destination, driverUpdate);
            log.info("Published Driver Update via WebSocket: driverId={}, orderId={}, status={}", 
                    driverUpdate.getDriverId(), driverUpdate.getOrderId(), driverUpdate.getStatus());
        } catch (Exception e) {
            log.error("Failed to publish Driver Update via WebSocket: driverId={}, orderId={}", 
                    driverUpdate.getDriverId(), driverUpdate.getOrderId(), e);
            throw e;
        }
    }

    /**
     * Publish order status updates to WebSocket for client portal
     */
    public void publishOrderStatusUpdate(String orderId, String status, Object payload) {
        try {
            String destination = "/topic/order/" + orderId;
            OrderStatusMessage message = OrderStatusMessage.builder()
                    .orderId(orderId)
                    .status(status)
                    .payload(payload)
                    .timestamp(java.time.LocalDateTime.now())
                    .build();
            messagingTemplate.convertAndSend(destination, message);
            log.info("Published Order Status Update via WebSocket: orderId={}, status={}", orderId, status);
        } catch (Exception e) {
            log.error("Failed to publish Order Status Update via WebSocket: orderId={}, status={}", orderId, status, e);
            throw e;
        }
    }
}
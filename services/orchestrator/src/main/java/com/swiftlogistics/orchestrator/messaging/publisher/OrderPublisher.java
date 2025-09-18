package com.swiftlogistics.orchestrator.messaging.publisher;

import com.swiftlogistics.orchestrator.config.RabbitMQConfig;
import com.swiftlogistics.orchestrator.dto.OrderMessage;
import com.swiftlogistics.orchestrator.dto.WarehouseRequestMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderPublisher {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Step 1: Publish order created event
     * This is consumed by CMS Adapter for billing
     */
    public void publishOrderCreated(OrderMessage orderMessage) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_ORDER_CREATED,
                    RabbitMQConfig.ROUTING_KEY_ORDER_CREATED,
                    orderMessage
            );
            log.info("Published Order Created event: orderId={}, correlationId={}", 
                    orderMessage.getOrderId(), orderMessage.getCorrelationId());
        } catch (Exception e) {
            log.error("Failed to publish Order Created event: orderId={}", orderMessage.getOrderId(), e);
            throw e;
        }
    }

    /**
     * Step 3: Publish order to warehouse queue after billing
     * This is consumed by WMS Adapter
     */
    public void publishToWarehouse(WarehouseRequestMessage warehouseRequest) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_WAREHOUSE,
                    RabbitMQConfig.ROUTING_KEY_WAREHOUSE_REQUEST,
                    warehouseRequest
            );
            log.info("Published Order to Warehouse: orderId={}, correlationId={}", 
                    warehouseRequest.getOrderId(), warehouseRequest.getCorrelationId());
        } catch (Exception e) {
            log.error("Failed to publish to Warehouse: orderId={}", warehouseRequest.getOrderId(), e);
            throw e;
        }
    }

    /**
     * Step 4: Publish route planning request
     * This is consumed by ROS Adapter
     */
    public void publishRoutePlanningRequest(OrderMessage orderMessage) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_ROUTE,
                    RabbitMQConfig.ROUTING_KEY_ROUTE_PLANNING,
                    orderMessage
            );
            log.info("Published Route Planning request: orderId={}, correlationId={}", 
                    orderMessage.getOrderId(), orderMessage.getCorrelationId());
        } catch (Exception e) {
            log.error("Failed to publish Route Planning request: orderId={}", orderMessage.getOrderId(), e);
            throw e;
        }
    }
}



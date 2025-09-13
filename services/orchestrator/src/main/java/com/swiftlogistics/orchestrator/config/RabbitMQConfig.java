package com.swiftlogistics.orchestrator.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // === OPTIMIZED EVENT-DRIVEN QUEUES ===
    
    // Step 1: Order Creation
    public static final String QUEUE_ORDER_CREATED = "order-created";
    public static final String EXCHANGE_ORDER_CREATED = "order-created.exchange";
    public static final String ROUTING_KEY_ORDER_CREATED = "order.created";
    
    // Step 2: Billing Updates
    public static final String QUEUE_BILLING_UPDATES = "billing-updates";
    public static final String EXCHANGE_BILLING_UPDATES = "billing-updates.exchange";
    public static final String ROUTING_KEY_BILLING_UPDATES = "billing.updated";
    
    // Step 3: Warehouse Operations
    public static final String QUEUE_WAREHOUSE_QUEUE = "warehouse-queue";
    public static final String QUEUE_WAREHOUSE_UPDATES = "warehouse-updates";
    public static final String EXCHANGE_WAREHOUSE = "warehouse.exchange";
    public static final String ROUTING_KEY_WAREHOUSE_REQUEST = "warehouse.request";
    public static final String ROUTING_KEY_WAREHOUSE_UPDATE = "warehouse.update";
    
    // Step 4: Route Planning
    public static final String QUEUE_ROUTE_PLANNING = "route-planning";
    public static final String QUEUE_ROUTE_UPDATES = "route-updates";
    public static final String EXCHANGE_ROUTE = "route.exchange";
    public static final String ROUTING_KEY_ROUTE_PLANNING = "route.planning";
    public static final String ROUTING_KEY_ROUTE_UPDATE = "route.update";
    
    // Step 5: Driver Operations
    public static final String QUEUE_DRIVER_UPDATES = "driver-updates";
    public static final String EXCHANGE_DRIVER = "driver.exchange";
    public static final String ROUTING_KEY_DRIVER_UPDATE = "driver.update";
    
    // Step 6: Delivery Operations
    public static final String QUEUE_DELIVERY_UPDATES = "delivery-updates";
    public static final String EXCHANGE_DELIVERY = "delivery.exchange";
    public static final String ROUTING_KEY_DELIVERY_UPDATE = "delivery.update";

    // Dead-letter queues
    public static final String DLQ_SUFFIX = ".dlq";
    public static final String DLX_SUFFIX = ".dlx";

    // === EXCHANGES ===
    
    @Bean
    public DirectExchange orderCreatedExchange() {
        return new DirectExchange(EXCHANGE_ORDER_CREATED, true, false);
    }
    
    @Bean
    public DirectExchange billingUpdatesExchange() {
        return new DirectExchange(EXCHANGE_BILLING_UPDATES, true, false);
    }
    
    @Bean
    public DirectExchange warehouseExchange() {
        return new DirectExchange(EXCHANGE_WAREHOUSE, true, false);
    }
    
    @Bean
    public DirectExchange routeExchange() {
        return new DirectExchange(EXCHANGE_ROUTE, true, false);
    }
    
    @Bean
    public DirectExchange driverExchange() {
        return new DirectExchange(EXCHANGE_DRIVER, true, false);
    }
    
    @Bean
    public DirectExchange deliveryExchange() {
        return new DirectExchange(EXCHANGE_DELIVERY, true, false);
    }

    // === QUEUES ===
    
    @Bean
    public Queue orderCreatedQueue() {
        return QueueBuilder.durable(QUEUE_ORDER_CREATED)
                .withArgument("x-dead-letter-exchange", EXCHANGE_ORDER_CREATED + DLX_SUFFIX)
                .withArgument("x-dead-letter-routing-key", QUEUE_ORDER_CREATED + DLQ_SUFFIX)
                .build();
    }
    
    @Bean
    public Queue billingUpdatesQueue() {
        return QueueBuilder.durable(QUEUE_BILLING_UPDATES)
                .withArgument("x-dead-letter-exchange", EXCHANGE_BILLING_UPDATES + DLX_SUFFIX)
                .withArgument("x-dead-letter-routing-key", QUEUE_BILLING_UPDATES + DLQ_SUFFIX)
                .build();
    }
    
    @Bean
    public Queue warehouseQueueRequest() {
        return QueueBuilder.durable(QUEUE_WAREHOUSE_QUEUE)
                .withArgument("x-dead-letter-exchange", EXCHANGE_WAREHOUSE + DLX_SUFFIX)
                .withArgument("x-dead-letter-routing-key", QUEUE_WAREHOUSE_QUEUE + DLQ_SUFFIX)
                .build();
    }
    
    @Bean
    public Queue warehouseUpdatesQueue() {
        return QueueBuilder.durable(QUEUE_WAREHOUSE_UPDATES)
                .withArgument("x-dead-letter-exchange", EXCHANGE_WAREHOUSE + DLX_SUFFIX)
                .withArgument("x-dead-letter-routing-key", QUEUE_WAREHOUSE_UPDATES + DLQ_SUFFIX)
                .build();
    }
    
    @Bean
    public Queue routePlanningQueue() {
        return QueueBuilder.durable(QUEUE_ROUTE_PLANNING)
                .withArgument("x-dead-letter-exchange", EXCHANGE_ROUTE + DLX_SUFFIX)
                .withArgument("x-dead-letter-routing-key", QUEUE_ROUTE_PLANNING + DLQ_SUFFIX)
                .build();
    }
    
    @Bean
    public Queue routeUpdatesQueue() {
        return QueueBuilder.durable(QUEUE_ROUTE_UPDATES)
                .withArgument("x-dead-letter-exchange", EXCHANGE_ROUTE + DLX_SUFFIX)
                .withArgument("x-dead-letter-routing-key", QUEUE_ROUTE_UPDATES + DLQ_SUFFIX)
                .build();
    }
    
    @Bean
    public Queue driverUpdatesQueue() {
        return QueueBuilder.durable(QUEUE_DRIVER_UPDATES)
                .withArgument("x-dead-letter-exchange", EXCHANGE_DRIVER + DLX_SUFFIX)
                .withArgument("x-dead-letter-routing-key", QUEUE_DRIVER_UPDATES + DLQ_SUFFIX)
                .build();
    }
    
    @Bean
    public Queue deliveryUpdatesQueue() {
        return QueueBuilder.durable(QUEUE_DELIVERY_UPDATES)
                .withArgument("x-dead-letter-exchange", EXCHANGE_DELIVERY + DLX_SUFFIX)
                .withArgument("x-dead-letter-routing-key", QUEUE_DELIVERY_UPDATES + DLQ_SUFFIX)
                .build();
    }

    // === BINDINGS ===
    
    @Bean
    public Binding bindingOrderCreated() {
        return BindingBuilder.bind(orderCreatedQueue()).to(orderCreatedExchange()).with(ROUTING_KEY_ORDER_CREATED);
    }
    
    @Bean
    public Binding bindingBillingUpdates() {
        return BindingBuilder.bind(billingUpdatesQueue()).to(billingUpdatesExchange()).with(ROUTING_KEY_BILLING_UPDATES);
    }
    
    @Bean
    public Binding bindingWarehouseRequest() {
        return BindingBuilder.bind(warehouseQueueRequest()).to(warehouseExchange()).with(ROUTING_KEY_WAREHOUSE_REQUEST);
    }
    
    @Bean
    public Binding bindingWarehouseUpdates() {
        return BindingBuilder.bind(warehouseUpdatesQueue()).to(warehouseExchange()).with(ROUTING_KEY_WAREHOUSE_UPDATE);
    }
    
    @Bean
    public Binding bindingRoutePlanning() {
        return BindingBuilder.bind(routePlanningQueue()).to(routeExchange()).with(ROUTING_KEY_ROUTE_PLANNING);
    }
    
    @Bean
    public Binding bindingRouteUpdates() {
        return BindingBuilder.bind(routeUpdatesQueue()).to(routeExchange()).with(ROUTING_KEY_ROUTE_UPDATE);
    }
    
    @Bean
    public Binding bindingDriverUpdates() {
        return BindingBuilder.bind(driverUpdatesQueue()).to(driverExchange()).with(ROUTING_KEY_DRIVER_UPDATE);
    }
    
    @Bean
    public Binding bindingDeliveryUpdates() {
        return BindingBuilder.bind(deliveryUpdatesQueue()).to(deliveryExchange()).with(ROUTING_KEY_DELIVERY_UPDATE);
    }
}

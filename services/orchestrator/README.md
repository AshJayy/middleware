# Orchestrator Service

## Overview

This service acts as the central coordination hub for the Swift Logistics platform. It manages the complete order lifecycle from creation to delivery through a 6-step event-driven architecture, coordinating with various adapters (CMS, WMS, ROS) and handling real-time updates.

## Prerequisites

- Java 21+
- Maven 3.6+
- MongoDB (running on localhost:27017 or cloud instance)
- RabbitMQ (running on localhost:5672)

## How to Run

Build the application:

```bash
mvn clean package -DskipTests
```

Run the application:

```bash
mvn spring-boot:run
```

Or run the JAR file:

```bash
java -jar target/orchestrator-0.0.1-SNAPSHOT.jar
```

### With Docker

Build the Docker image:

```bash
mvn clean package -DskipTests
docker-compose build orchestrator
```

Run with Docker Compose:

```bash
docker-compose up -d
```

## Debug Mode

To enable debug logging, set the environment variable before running:
`LOGGING_LEVEL_COM_SWIFTLOGISTICS=DEBUG`

- This will show detailed event processing and message flow information.

## What Happens

- The orchestrator coordinates a 6-step order processing flow: Order Creation → Billing → Warehouse → Route Planning → Driver Assignment → Delivery
- It communicates with adapters via RabbitMQ messaging
- Order states and events are persisted in MongoDB
- Real-time updates are provided via WebSocket connections
- Complete audit trail is maintained for all order events

## File Structure

- `OrchestratorApplication.java`: Entry point for the Spring Boot service
- `controller/`: REST API endpoints for order management
- `messaging/`: RabbitMQ publishers and subscribers for event handling
- `model/`: Data models for orders, events, and messages
- `service/`: Business logic for order processing and event management
- `repository/`: Data access layer for MongoDB
- `config/`: Configuration classes for RabbitMQ, WebSocket, and CORS

## API Endpoints

| Method | Endpoint                               | Description           |
| ------ | -------------------------------------- | --------------------- |
| `POST` | `/api/v1/orders`                       | Create new order      |
| `GET`  | `/api/v1/orders/{orderId}`             | Get order details     |
| `GET`  | `/api/v1/orders/customer/{customerId}` | Get customer orders   |
| `GET`  | `/api/v1/orders/status/{status}`       | Get orders by status  |
| `GET`  | `/api/v1/orders/{orderId}/events`      | Get order audit trail |
| `GET`  | `/api/v1/orders/health`                | Service health check  |

### Create Order Example

```bash
curl -X POST http://localhost:8000/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST001",
    "customerName": "John Doe",
    "customerEmail": "john.doe@example.com",
    "deliveryAddress": "123 Main St",
    "city": "New York",
    "postalCode": "10001",
    "country": "USA",
    "totalAmount": 99.99
  }'
```

## Message Queues

The orchestrator uses 8 specialized queues for event-driven communication:

- `order-created`: Order creation events
- `billing-updates`: Billing status updates
- `warehouse-queue`: Warehouse requests
- `warehouse-updates`: Warehouse status updates
- `route-planning`: Route planning requests
- `route-updates`: Route planning updates
- `driver-updates`: Driver assignment updates
- `delivery-updates`: Delivery status updates

## Notes

The orchestrator coordinates with multiple adapter services (CMS, WMS, ROS) via RabbitMQ messaging.
Docker setup includes separate containers for RabbitMQ and the orchestrator service for better isolation.
MongoDB is used for persistent storage of orders and event audit trails.
WebSocket connections provide real-time updates to connected clients.
The service runs on port 8000 and RabbitMQ management UI is available on port 15672 (guest/guest).

# Orchestrator Service

## Overview

This service acts as the central coordination hub for the Swift Logistics platform. It manages the complete order lifecycle from creation to delivery through a 6-step event-driven architecture, coordinating with various adapters (CMS, WMS, ROS) and handling real-time updates via Server-Sent Events (SSE).

## Prerequisites

- Java 21+
- MongoDB (running on localhost:27017 or cloud instance)
- RabbitMQ (running on localhost:5672)

Note: Maven is not required as the project uses Maven Wrapper (`mvnw`/`mvnw.cmd`) which is included in the repository.

## How to Run

### Environment Setup

Copy the environment configuration file:

```bash
copy .env.example .env
```

Update the `.env` file with your specific configuration values.

Build the application:

```bash
./mvnw clean package -DskipTests
```

Run the application:

```bash
./mvnw spring-boot:run
```

Or run the JAR file:

```bash
java -jar target/orchestrator-0.0.1-SNAPSHOT.jar
```

### With Docker

Build the Docker image:

```bash
docker build -t orchestrator .
```

Or build with Docker Compose:

```bash
docker-compose build orchestrator
```

Run with Docker Compose:

```bash
docker-compose up -d
```

The Docker image uses a multi-stage build process:

- **Build stage**: Uses `maven:3.9.9-eclipse-temurin-21` to compile the application with Maven Wrapper
- **Runtime stage**: Uses `eclipse-temurin:21-jre-alpine` for a lightweight production image
- **JVM Options**: Set the `JAVA_OPTS` environment variable to pass additional JVM parameters

## What Happens

- The orchestrator coordinates a 6-step order processing flow: Order Creation → Billing → Warehouse → Route Planning → Driver Assignment → Delivery
- It communicates with adapters via RabbitMQ messaging
- Order states and events are persisted in MongoDB
- Real-time updates are provided via Server-Sent Events (SSE) connections
- Authentication system for both customers and drivers
- Complete audit trail is maintained for all order events

## File Structure

```
src/
├── main/
│   ├── java/com/swiftlogistics/orchestrator/
│   │   ├── OrchestratorApplication.java          # Entry point for Spring Boot service
│   │   ├── config/                               # Configuration classes
│   │   │   ├── RabbitMQConfig.java              # RabbitMQ queue and exchange configuration
│   │   │   ├── RabbitTemplateConfig.java        # RabbitMQ template configuration
│   │   │   └── WebConfig.java                   # CORS and WebSocket configuration
│   │   ├── controller/                          # REST API endpoints
│   │   │   ├── AuthController.java              # Authentication endpoints for customers and drivers
│   │   │   ├── DriverController.java            # Driver-specific order management
│   │   │   ├── OrderController.java             # Main order management endpoints
│   │   │   └── SseController.java               # Server-Sent Events for real-time updates
│   │   ├── dto/                                 # Data Transfer Objects
│   │   │   ├── BillingUpdateMessage.java        # Billing update message structure
│   │   │   ├── CreateOrderRequest.java          # Order creation request structure
│   │   │   ├── DriverUpdateMessage.java         # Driver update message structure
│   │   │   ├── LoginRequest.java                # Authentication request structure
│   │   │   ├── OrderMessage.java                # Order message structure
│   │   │   ├── OrderStatusMessage.java          # Order status message structure
│   │   │   ├── RouteRequestMessage.java         # Route planning request structure
│   │   │   ├── RouteUpdateMessage.java          # Route update message structure
│   │   │   ├── WarehouseRequestMessage.java     # Warehouse request message structure
│   │   │   └── WarehouseUpdateMessage.java      # Warehouse update message structure
│   │   ├── messaging/                           # RabbitMQ messaging components
│   │   │   ├── publisher/                       # Message publishers
│   │   │   │   ├── OrderPublisher.java          # Order event publishing
│   │   │   │   └── SsePublisher.java            # SSE event publishing
│   │   │   └── subscriber/                      # Message subscribers
│   │   │       ├── BillingUpdateSubscriber.java # Billing update handling
│   │   │       ├── RouteSubscriber.java         # Route update handling
│   │   │       └── WarehouseSubscriber.java     # Warehouse update handling
│   │   ├── model/                               # Data models
│   │   │   ├── Customer.java                    # Customer entity
│   │   │   ├── Driver.java                      # Driver entity
│   │   │   ├── Event.java                       # Event audit trail entity
│   │   │   ├── Order.java                       # Order entity
│   │   │   ├── Route.java                       # Route entity
│   │   │   └── enums/                           # Enumeration classes
│   │   │       ├── EventSource.java             # Event source types
│   │   │       ├── EventStatus.java             # Event status types
│   │   │       ├── EventType.java               # Event types
│   │   │       └── OrderStatus.java             # Order status types
│   │   ├── repository/                          # Data access layer for MongoDB
│   │   │   ├── CustomerRepository.java          # Customer data access
│   │   │   ├── DriverRepository.java            # Driver data access
│   │   │   ├── EventRepository.java             # Event data access
│   │   │   ├── OrderRepository.java             # Order data access
│   │   │   └── RouteRepository.java             # Route data access
│   │   └── service/                             # Business logic services
│   │       ├── DriverService.java               # Driver-related business logic
│   │       ├── EventService.java                # Event management and audit trail
│   │       ├── OrderService.java                # Order processing and lifecycle management
│   │       └── RouteService.java                # Route planning and management
│   └── resources/
│       ├── application.properties               # Application configuration
│       ├── static/                              # Static web resources
│       └── templates/                           # Template files
└── test/
    └── java/com/swiftlogistics/orchestrator/
        └── OrchestratorApplicationTests.java    # Application tests
```

## API Endpoints

### Order Management

| Method | Endpoint                               | Description                      |
| ------ | -------------------------------------- | -------------------------------- |
| `POST` | `/api/v1/orders`                       | Create new order                 |
| `GET`  | `/api/v1/orders/{orderId}`             | Get order details                |
| `GET`  | `/api/v1/orders/customer/{customerId}` | Get orders for specific customer |
| `GET`  | `/api/v1/orders/{orderId}/events`      | Get order audit trail            |
| `GET`  | `/api/v1/orders/health`                | Service health check             |

### Driver Management

| Method | Endpoint                                   | Description                   |
| ------ | ------------------------------------------ | ----------------------------- |
| `GET`  | `/api/v1/orders/driver/{driverId}`         | Get pending orders for driver |
| `GET`  | `/api/v1/orders/driver/{orderId}`          | Get order details for driver  |
| `PUT`  | `/api/v1/orders/driver/start/{orderId}`    | Start delivery for order      |
| `PUT`  | `/api/v1/orders/driver/complete/{orderId}` | Complete delivery for order   |

### Authentication

| Method | Endpoint             | Description    |
| ------ | -------------------- | -------------- |
| `POST` | `/api/auth/customer` | Customer login |
| `POST` | `/api/auth/driver`   | Driver login   |

### Real-time Updates (Server-Sent Events)

| Method | Endpoint               | Description                          |
| ------ | ---------------------- | ------------------------------------ |
| `GET`  | `/sse/order/{orderId}` | Subscribe to real-time order updates |

## Message Queues

The orchestrator uses 8 specialized queues for event-driven communication:

- `order-created`: Order creation events
- `billing-updates`: Billing status updates
- `warehouse-queue`: Warehouse requests
- `warehouse-updates`: Warehouse status updates
- `route-planning`: Route planning requests
- `route-updates`: Route planning updates

## Configuration

The service uses environment variables for configuration. Key settings include:

- `SERVER_PORT`: Application port (default: 8000)
- `MONGODB_URI`: MongoDB connection string
- `RABBITMQ_HOST`, `RABBITMQ_PORT`: RabbitMQ connection details
- `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`: RabbitMQ credentials
- `JAVA_OPTS`: JVM options for Docker deployment (e.g., `-Xmx512m -Xms256m`)

## Example API Usage

### Create Order

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

### Customer Login

```bash
curl -X POST http://localhost:8000/api/auth/customer \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "password": "password123"
  }'
```

### Driver Login

```bash
curl -X POST http://localhost:8000/api/auth/driver \
  -H "Content-Type: application/json" \
  -d '{
    "username": "driver001",
    "password": "driverpass"
  }'
```

### Subscribe to Order Updates (SSE)

```bash
curl -N http://localhost:8000/sse/order/ORDER123
```

## Notes

- The orchestrator coordinates with multiple adapter services (CMS, WMS, ROS) via RabbitMQ messaging
- Docker setup uses a multi-stage build for optimized image size with Alpine Linux base
- MongoDB is used for persistent storage of orders, events, customers, drivers, and routes
- Server-Sent Events (SSE) provide real-time updates to connected clients
- The service runs on port 8000 by default
- RabbitMQ management UI is available on port 15672 (guest/guest)
- Authentication is implemented for both customers and drivers with mock functionality
- Complete audit trail is maintained for all order events and state changes
- Maven Wrapper (`mvnw`/`mvnw.cmd`) eliminates the need for a local Maven installation

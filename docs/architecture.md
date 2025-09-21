# SwiftLogistics - Middleware Architecture

## Table of Contents
1. [Overview](#overview)
2. [System Architecture](#system-architecture)
3. [Component Details](#component-details)
4. [Data Flow](#data-flow)
5. [Integration Patterns](#integration-patterns)
6. [Technology Stack](#technology-stack)
7. [Messaging Architecture](#messaging-architecture)
8. [Database Design](#database-design)
9. [API Specifications](#api-specifications)
10. [Deployment Architecture](#deployment-architecture)

## Overview

SwiftLogistics is a comprehensive middleware architecture designed to integrate three disparate legacy systems:
- **CMS (Customer Management System)** - SOAP/XML based
- **ROS (Route Optimization System)** - REST API based  
- **WMS (Warehouse Management System)** - TCP/IP messaging based

The architecture implements an event-driven microservices pattern using message queues to ensure loose coupling, scalability, and fault tolerance across the integrated systems.

## System Architecture

### High-Level Architecture Diagram

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Client Portal │    │   Driver App    │    │  Admin Portal   │
│   (React SPA)   │    │  (React PWA)    │    │   (Future)      │
└─────────┬───────┘    └─────────┬───────┘    └─────────────────┘
          │                      │
          └──────────┬───────────┘
                     │
          ┌──────────▼───────────┐
          │    Load Balancer     │
          │     (Nginx)          │
          └──────────┬───────────┘
                     │
          ┌──────────▼───────────┐
          │   Orchestrator API   │
          │   (Spring Boot)      │
          │     Port: 8000       │
          └──────────┬───────────┘
                     │
          ┌──────────▼───────────┐
          │     RabbitMQ         │
          │  Message Broker      │
          │   Ports: 5672/15672  │
          └─────┬────┬────┬──────┘
                │    │    │
        ┌───────▼─┐ ┌▼────▼──┐ ┌─▼──────────┐
        │CMS      │ │ROS     │ │WMS         │
        │Adapter  │ │Integr. │ │Adapter     │
        │Port:8001│ │Port:8004│ │Port:8003   │
        └───┬─────┘ └────┬───┘ └─┬──────────┘
            │            │       │
        ┌───▼─────┐ ┌────▼───┐ ┌─▼──────────┐
        │Legacy   │ │ROS     │ │Legacy      │
        │CMS      │ │Server  │ │WMS         │
        │(SOAP)   │ │(REST)  │ │(TCP/IP)    │
        └─────────┘ └────────┘ └────────────┘
```

### Core Architectural Principles

1. **Event-Driven Architecture**: All inter-service communication happens through asynchronous messaging
2. **Microservices Pattern**: Each system integration is handled by a dedicated adapter service
3. **Loose Coupling**: Services communicate only through well-defined message contracts
4. **Fault Tolerance**: Message queues provide durability and retry mechanisms
5. **Scalability**: Each service can be scaled independently based on load

## Component Details

### 1. Orchestrator Service (Spring Boot)
**Location**: `services/orchestrator/`
**Port**: 8000
**Responsibilities**:
- Central coordination of order lifecycle
- RESTful API for client applications
- Message routing and transformation
- Order state management
- Real-time updates via Server-Sent Events (SSE)
- Driver assignment and management

**Key Features**:
- MongoDB integration for persistent storage
- RabbitMQ message publishing/consuming
- JWT-based authentication (planned)
- Comprehensive logging and monitoring

### 2. CMS Adapter Service (Spring Boot)
**Location**: `services/cms-adapter/`
**Port**: 8001
**Responsibilities**:
- SOAP to REST protocol translation
- Order creation and customer management
- Billing integration
- Legacy CMS system integration

**Integration Pattern**:
```
REST API ←→ CMS Adapter ←→ SOAP/XML ←→ Legacy CMS
```

### 3. ROS Integration Service (FastAPI/Python)
**Location**: `services/ros-integration/`
**Port**: 8004
**Responsibilities**:
- Route optimization requests
- Geographic waypoint processing
- Integration with route planning algorithms
- Real-time route updates

**Message Flow**:
1. Consumes `route-requests` from queue
2. Calls ROS mock server for optimization
3. Publishes optimized routes to `route-updates` queue

### 4. WMS Adapter Service (FastAPI/Python)
**Location**: `services/wms-adapter/`
**Port**: 8003
**Responsibilities**:
- TCP/IP to REST protocol conversion
- Warehouse inventory tracking
- Package status updates
- Custom message parsing for legacy WMS

**Protocol Translation**:
```
REST/JSON ←→ WMS Adapter ←→ TCP/IP Messages ←→ Legacy WMS
```

### 5. Client Portal (React SPA)
**Location**: `ui/client-portal/`
**Port**: 3000
**Features**:
- Order submission and tracking
- Real-time status updates via SSE
- Customer dashboard
- Responsive design for multiple devices

### 6. Driver Application (React PWA)
**Location**: `ui/driver-app/`
**Port**: 3001
**Features**:
- Route visualization
- Delivery status updates
- Offline capability (PWA)
- Mobile-first design

## Data Flow

### Order Processing Flow

```
1. Order Creation
   Client Portal → Orchestrator API → MongoDB
                                   ↓
                            RabbitMQ (order-events)
                                   ↓
                            CMS Adapter → Legacy CMS
                                   ↓
                            Billing Update → Orchestrator

2. Route Planning
   Orchestrator → RabbitMQ (route-requests)
                           ↓
                    ROS Integration → ROS Server
                           ↓
                    RabbitMQ (route-updates)
                           ↓
                    Orchestrator → Driver Assignment

3. Warehouse Processing
   WMS System → TCP Messages → WMS Adapter
                                    ↓
                            RabbitMQ (warehouse-updates)
                                    ↓
                            Orchestrator → Status Update

4. Real-time Updates
   Orchestrator → SSE → Client Portal
                    → SSE → Driver App
```

### Message Queue Architecture

**Queue Configuration**:
```
order-events       → CMS Adapter consumption
route-requests     → ROS Integration consumption  
route-updates      → Orchestrator consumption
warehouse-updates  → Orchestrator consumption
billing-updates    → Orchestrator consumption
driver-updates     → Driver App notifications
```

## Integration Patterns

### 1. Protocol Translation Pattern
Each adapter service implements protocol translation:
- **CMS Adapter**: REST ↔ SOAP/XML
- **WMS Adapter**: REST ↔ TCP/IP
- **ROS Integration**: Message Queue ↔ REST

### 2. Event Sourcing Pattern
All state changes are captured as events in RabbitMQ queues, providing:
- Audit trail
- Replay capability  
- Debugging support
- System recovery

### 3. CQRS (Command Query Responsibility Segregation)
- **Commands**: Handled through message queues
- **Queries**: Direct API calls to Orchestrator
- **Read Models**: Optimized for UI consumption

## Technology Stack

| Component | Technology | Version | Purpose |
|-----------|------------|---------|---------|
| **Backend Services** |
| Orchestrator | Spring Boot | 3.2+ | Main coordination service |
| CMS Adapter | Spring Boot | 3.2+ | SOAP integration |
| ROS Integration | FastAPI | 0.104+ | Python-based route service |
| WMS Adapter | FastAPI | 0.104+ | TCP/IP integration |
| **Message Broker** |
| RabbitMQ | Docker | 3.12-management | Async messaging |
| **Database** |
| MongoDB | Docker | 7.0+ | Document storage |
| **Frontend** |
| Client Portal | React | 18+ | Customer interface |
| Driver App | React PWA | 18+ | Driver interface |
| **Infrastructure** |
| Docker | Containerization | 24+ | Service deployment |
| Docker Compose | Orchestration | 2.21+ | Multi-container apps |

## Messaging Architecture

### RabbitMQ Configuration

**Exchanges**:
- `swift.direct` - Direct routing for specific services
- `swift.topic` - Topic-based routing for broadcast messages

**Queues and Routing**:
```yaml
Queues:
  order-events:
    routing-key: "order.created"
    consumer: cms-adapter
    
  route-requests:
    routing-key: "route.optimize"
    consumer: ros-integration
    
  route-updates:
    routing-key: "route.completed"  
    consumer: orchestrator
    
  warehouse-updates:
    routing-key: "warehouse.*"
    consumer: orchestrator
    
  billing-updates:
    routing-key: "billing.processed"
    consumer: orchestrator
```

### Message Formats

**Order Event Message**:
```json
{
  "eventId": "uuid",
  "eventType": "ORDER_CREATED",
  "orderId": "order-uuid", 
  "customerId": "customer-uuid",
  "timestamp": "2025-09-21T10:30:00Z",
  "data": {
    "deliveryAddress": "123 Main St",
    "city": "Colombo",
    "postalCode": "10100",
    "country": "Sri Lanka"
  }
}
```

**Route Update Message**:
```json
{
  "orderId": "order-uuid",
  "status": "ROUTED",
  "waypoints": ["Colombo", "Kottawa", "Kadawatha"],
  "driverId": "driver-uuid",
  "vehicleId": "vehicle-123",
  "timestamp": "2025-09-21T10:35:00Z"
}
```

## Database Design

### MongoDB Collections

**Orders Collection**:
```javascript
{
  _id: ObjectId,
  orderId: String (UUID),
  customerId: String (UUID),
  deliveryAddress: String,
  city: String,
  postalCode: String,
  country: String,
  status: Enum [CREATED, BILLED, ROUTED, IN_TRANSIT, DELIVERED],
  createdAt: DateTime,
  updatedAt: DateTime,
  driverId: String (UUID),
  routeId: String (UUID),
  billedAt: DateTime,
  packageReadyAt: DateTime,
  routedAt: DateTime
}
```

**Drivers Collection**:
```javascript
{
  _id: ObjectId,
  driverName: String,
  vehicleId: String,
  isAvailable: Boolean,
  type: Enum [bike, van, truck],
  password: String (hashed),
  currentLocation: {
    latitude: Number,
    longitude: Number
  }
}
```

**Customers Collection**:
```javascript
{
  _id: ObjectId,
  customerId: String (UUID),
  name: String,
  email: String,
  phone: String,
  address: {
    street: String,
    city: String,
    postalCode: String,
    country: String
  }
}
```

## API Specifications

### Orchestrator REST API

**Base URL**: `http://localhost:8000/api/v1`

**Endpoints**:

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| POST | `/orders` | Create new order | Order details |
| GET | `/orders` | List all orders | Query params |
| GET | `/orders/{id}` | Get order by ID | - |
| PUT | `/orders/{id}/status` | Update order status | Status object |
| GET | `/drivers` | List available drivers | - |
| POST | `/drivers/{id}/assign` | Assign driver to order | Assignment data |
| GET | `/events/stream` | SSE event stream | - |

**Example API Calls**:

```bash
# Create Order
curl -X POST http://localhost:8000/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "cust-123",
    "deliveryAddress": "123 Main Street",
    "city": "Colombo",
    "postalCode": "10100",
    "country": "Sri Lanka"
  }'

# Get Order Status  
curl http://localhost:8000/api/v1/orders/order-uuid

# Stream Real-time Updates
curl -N http://localhost:8000/api/v1/events/stream
```

## Deployment Architecture

### Docker Composition

The system deploys as a multi-container Docker application:

**Service Dependencies**:
```
┌─────────────┐
│  RabbitMQ   │ ← Health Check Required
└──────┬──────┘
       │
   ┌───▼────┐ ┌──────────┐ ┌───────────┐ ┌─────────────┐
   │Orchest.│ │CMS Adapt.│ │ROS Integr.│ │WMS Adapter  │
   └────────┘ └──────────┘ └───────────┘ └─────────────┘
```

**Network Configuration**:
- All services run on `swift-network` bridge network
- External ports exposed only for API endpoints and UI
- Internal service discovery via container names

**Volume Management**:
- `rabbitmq_data`: Persistent message storage
- `orchestrator_logs`: Centralized logging
- `mongo_data`: Database persistence

### Environment Configuration

**Required Environment Variables**:

```bash
# RabbitMQ Configuration
RABBITMQ_DEFAULT_USER=admin
RABBITMQ_DEFAULT_PASS=admin123

# MongoDB Configuration  
MONGODB_URI=mongodb://mongo:27017/swiftlogistics
MONGODB_DATABASE=swiftlogistics

# Service URLs
CMS_ADAPTER_URL=http://cms-adapter:8001
ROS_INTEGRATION_URL=http://ros-integration:8004
WMS_ADAPTER_URL=http://wms-adapter:8003

# Application Configuration
SPRING_PROFILES_ACTIVE=docker
SERVER_PORT=8000
```

### Build and Deployment Process

**Step 1: Infrastructure Setup**
```bash
cd infra
docker-compose build
docker-compose up -d
```

**Step 2: Service Health Verification**
```bash
# Check RabbitMQ
curl http://localhost:15672

# Check Orchestrator API
curl http://localhost:8000/api/v1/health

# Check Individual Services
curl http://localhost:8001/health  # CMS Adapter
curl http://localhost:8003/health  # WMS Adapter  
curl http://localhost:8004/health  # ROS Integration
```

**Step 3: Access Applications**
- Client Portal: http://localhost:3000
- Driver App: http://localhost:3001
- RabbitMQ Management: http://localhost:15672

### Monitoring and Observability

**Health Checks**:
- All services implement `/health` endpoints
- Docker health checks configured for critical services
- RabbitMQ diagnostics for message broker status

**Logging Strategy**:
- Structured JSON logging across all services
- Centralized log aggregation (planned: ELK stack)
- Request/response logging with correlation IDs

**Metrics Collection**:
- Application metrics via Spring Boot Actuator
- Message queue metrics from RabbitMQ Management
- Custom business metrics for order processing

This architecture provides a robust, scalable foundation for integrating disparate legacy systems while maintaining modern development practices and operational excellence.

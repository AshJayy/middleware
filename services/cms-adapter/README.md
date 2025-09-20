# CMS Adapter Service

A Spring Boot microservice that acts as an adapter between the SwiftLogistics system and external CMS (Content Management System) services. This service handles order processing through SOAP web services and manages billing updates via RabbitMQ messaging.

## Overview

The CMS Adapter is part of the SwiftLogistics middleware architecture, designed to:
- Consume order messages from RabbitMQ queues
- Process orders through external CMS SOAP services
- Publish billing updates back to the message queue
- Provide health monitoring endpoints

## Features

- **Message-Driven Architecture**: Consumes order messages from RabbitMQ
- **SOAP Web Services**: Integrates with external CMS systems using SOAP
- **Auto-Generated Client Code**: Uses Apache CXF to generate SOAP client code from WSDL
- **Health Monitoring**: Provides health check endpoints

## Technology Stack

- **Java 21** - Programming language
- **Spring Boot 3.5.5** - Application framework
- **Apache CXF 4.0.5** - SOAP web services
- **RabbitMQ** - Message broker
- **Maven** - Build tool

## Project Structure

```
src/
├── main/
│   ├── java/com/swiftlogistics/cms_adapter/
│   │   ├── CmsAdapterApplication.java          # Main application class
│   │   ├── config/
│   │   │   ├── RabbitMQConfig.java            # RabbitMQ configuration
│   │   │   ├── RabbitTemplateConfig.java      # RabbitMQ template setup
│   │   │   └── SoapClientConfig.java          # SOAP client configuration
│   │   ├── consumer/
│   │   │   └── OrderConsumer.java             # RabbitMQ message consumer
│   │   ├── controller/
│   │   │   └── HealthController.java          # Health check endpoint
│   │   ├── model/
│   │   │   ├── OrderMessage.java              # Order data model
│   │   │   └── BillingUpdateMessage.java      # Billing update model
│   │   ├── producer/
│   │   │   └── BillingUpdatePublisher.java    # RabbitMQ message publisher
│   │   └── soap/
│   │       └── CmsSoapClient.java             # SOAP client implementation
│   └── resources/
│       ├── application.properties             # Application configuration
│       └── wsdl/
│           └── orderService.wsdl              # SOAP service definition
└── test/
    └── java/com/swiftlogistics/cms_adapter/
        └── CmsAdapterApplicationTests.java    # Unit tests
```

## Message Flow

### Order Processing Flow
1. **Order Created**: Orchestrator publishes order to `order-created` queue
2. **Order Consumption**: CMS Adapter consumes the order message
3. **SOAP Call**: Adapter calls external CMS SOAP service with order details
4. **Billing Update**: On success, publishes billing update to `billing-updates` queue

### Queue Configuration
- **Input Queue**: `order-created` - Receives order messages from orchestrator
- **Output Queue**: `billing-updates` - Sends billing updates back to orchestrator

[//]: # (- **Dead Letter Queues**: Configured for error handling and message recovery)

## Configuration

### Environment Variables
```bash
# RabbitMQ Configuration
SPRING_RABBITMQ_HOST=localhost
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=guest
SPRING_RABBITMQ_PASSWORD=guest

# CMS SOAP Service Configuration
CMS_SOAP_ENDPOINT=http://localhost:5000/cms/orders

# Application Configuration
SERVER_PORT=8082
```

### Application Properties
Configure the application by setting properties in `application.properties` or through environment variables.

## Getting Started

### Prerequisites
- Java 21 or higher
- Maven 3.6+
- RabbitMQ server
- Docker (for containerized deployment)

### Local Development

1. **Build the application**
   ```bash
   mvn clean compile
   ```

2. **Run tests**
   ```bash
   mvn test
   ```

3. **Start the application**
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8082`

### Docker Deployment

1. **Build Docker image**
   ```bash
   docker build -t cms-adapter:latest .
   ```

2. **Run container**
   ```bash
   docker run -p 8082:8082 \
     -e SPRING_RABBITMQ_HOST=your-rabbitmq-host \
     -e CMS_SOAP_ENDPOINT=your-cms-endpoint \
     cms-adapter:latest
   ```

3. **Run with Docker Compose** (if you have a compose file)
   ```bash
   docker-compose up -d
   ```

## API Endpoints

### Health Check
- **GET** `/health` - Returns application health status
  ```bash
  curl http://localhost:8082/health
  ```
  Response: `CMS Adapter is running!`

## Message Schemas

### Order Message (Input)
```json
{
  "orderId": "ORD-12345",
  "customerId": "CUST-001",
  "customerName": "John Doe",
  "customerEmail": "john.doe@example.com",
  "deliveryAddress": "123 Main St",
  "city": "New York",
  "postalCode": "10001",
  "country": "USA",
  "totalAmount": 99.99,
  "timestamp": "2025-09-20T10:30:00"
}
```

### Billing Update Message (Output)
```json
{
  "orderId": "ORD-12345",
  "amount": 99.99,
  "status": "BILLED",
  "timestamp": "2025-09-20T10:30:05"
}
```

## Monitoring and Logging

- Application logs are output to console in structured format
- Health endpoint available at `/health`
- RabbitMQ message processing is logged with order details
- SOAP service calls are logged for debugging

[//]: # (## Error Handling)

[//]: # ()
[//]: # (- **Dead Letter Queues**: Failed messages are routed to DLQ for manual inspection)

[//]: # (- **Retry Logic**: Configurable retry attempts for failed SOAP calls)

[//]: # (- **Circuit Breaker**: Protection against external service failures)

[//]: # (- **Graceful Degradation**: Application continues running even if external services are down)

## Development

### Code Generation
SOAP client code is automatically generated from WSDL files using Apache CXF Maven plugin:
```bash
mvn generate-sources
```

### Adding New SOAP Services
1. Place WSDL file in `src/main/resources/wsdl/`
2. Update `pom.xml` CXF plugin configuration
3. Run `mvn generate-sources`
4. Implement client wrapper in `soap/` package

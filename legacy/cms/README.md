# CMS Mock Service

Simple SOAP service that mocks CMS billing functionality.

## Quick Start

```bash
# Build and run with Docker (no need to build jar first!)
docker-compose up --build

# View logs
docker-compose logs -f

# Stop
docker-compose down
```

## Endpoints

- **SOAP Service:** http://localhost:5001/ws
- **WSDL:** http://localhost:5001/ws/orders.wsdl

## Test Request

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:ord="http://swiftlogistics.com/cms/orders">
   <soapenv:Body>
      <ord:SubmitOrderRequest>
         <ord:orderId>ORD-001</ord:orderId>
         <ord:clientId>CLIENT-123</ord:clientId>
         <ord:amount>99.99</ord:amount>
      </ord:SubmitOrderRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

## Test Response

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:ord="http://swiftlogistics.com/cms/orders">
   <soapenv:Body>
      <ord:SubmitOrderResponse>
         <ord:status>BILLED</ord:status>
         <ord:message>Order ORD-001 successfully processed and billed by CMS mock</ord:message>
         <ord:orderId>ORD-001</ord:orderId>
         <ord:billedAmount>99.99</ord:billedAmount>
      </ord:SubmitOrderResponse>
   </soapenv:Body>
</soapenv:Envelope>
```

Returns SUCCESS status immediately with order ID and billed amount (mock behavior).

## Features

- **Multi-stage Docker build**: Application builds inside container using Eclipse Temurin JDK 21, runs with JRE 21
- **Optimized Docker layers**: Maven dependencies cached separately for faster rebuilds
- **Configurable port**: Runs on port 5000 (configurable via SERVER_PORT environment variable)
- **Enhanced response**: Returns order ID and billed amount in addition to status and message
- **Mock billing**: Immediately marks orders as BILLED for testing purposes
- **SOAP/WSDL compliant**: Full XML schema definitions included
- **Memory optimized**: JVM configured with -Xmx512m -Xms256m for container efficiency

## Docker Configuration

The application uses a multi-stage Dockerfile:
- **Stage 1 (Builder)**: Uses Eclipse Temurin JDK 21 to build the application with Maven
- **Stage 2 (Runtime)**: Uses Eclipse Temurin JRE 21 for a smaller production image
- **Port**: Exposes and runs on port 5000
- **Environment**: Configurable via SERVER_PORT and JAVA_OPTS environment variables

## Development

```bash
# Local development (requires Maven and Java 21)
mvn spring-boot:run

# Run tests
mvn test

# Build jar locally (optional)
mvn clean package

# Run with custom port
SERVER_PORT=5000 mvn spring-boot:run
```

## Environment Variables

- `SERVER_PORT`: Application port (default: 5000)
- `SPRING_APPLICATION_NAME`: Application name (default: cms-mock)
- `JAVA_OPTS`: JVM options for Docker container

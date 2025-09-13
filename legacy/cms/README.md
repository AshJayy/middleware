# CMS Mock Service

Simple SOAP service that mocks CMS billing functionality.

## Quick Start

```bash
# Build the jar first
mvn clean package

# Build and run with Docker
docker-compose up --build

# View logs
docker-compose logs -f

# Stop
docker-compose down
```

## Endpoints

- **SOAP Service:** http://localhost:8081/ws
- **WSDL:** http://localhost:8081/ws/orders.wsdl

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

Returns SUCCESS status immediately (mock behavior).

# SwiftLogistics - Middleware Architecture Prototype

This project is a prototype middleware solution for **SwiftLogistics**, integrating three systems:
- **CMS (SOAP/XML)**
- **ROS (REST API)**
- **WMS (TCP/IP Messaging)**

The solution demonstrates a minimal **order submission → route planning → warehouse tracking → client/driver UI** flow.

---

## Project Structure

```
swiftlogistics/
│── docs/ # Documentation & diagrams
│── services/ # All backend services
│ ├── cms-adapter/ # SOAP → REST adapter
│ ├── ros-integration/ # REST client for route optimization
│ ├── wms-adapter/ # TCP/IP → REST/JSON adapter
│ └── orchestrator/ # Messaging layer + flow orchestration
│── ui/ # Frontend apps
│ ├── client-portal/ # Web dashboard for clients
│ └── driver-app/ # Mobile-like driver UI (React PWA)
│── infra/ # Infrastructure setup (Docker, etc.)
└── README.mdv
```


---

## Tech Stack & Ports

| Component             | Tech/Framework   | Port |
|----------------------|----------------|------|
| CMS Adapter          | FastAPI / Spring Boot | **8001** |
| ROS Integration      | FastAPI / Node.js    | **8002** |
| WMS Adapter          | FastAPI / Node.js    | **8003** |
| Orchestrator API     | FastAPI / Node.js    | **8000** |
| RabbitMQ Management  | Docker Container     | **15672** (UI) |
| RabbitMQ Broker      | Docker Container     | **5672** (AMQP) |
| Client Portal        | React (Vite/CRA)     | **3000** |
| Driver App           | React (Vite/CRA)     | **3001** |

> Make sure each service runs on its own port to avoid conflicts.  
> Update `.env` files or configs if you change ports.

## Getting Started

### 1 Spin up Infrastructure
```bash
cd infra
docker-compose up -d
```

This will start:

- RabbitMQ for async messaging.
- Postgres (optional, if persistence needed).

Check RabbitMQ dashboard at: http://localhost:15672

(Default user/pass: guest / guest)

### 2 Run Services (Backend)

Open separate terminals for each service:

``` bash
# CMS Adapter
cd services/cms-adapter
uvicorn main:app --reload --port 8001

# ROS Integration
cd services/ros-integration
uvicorn main:app --reload --port 8002

# WMS Adapter
cd services/wms-adapter
uvicorn main:app --reload --port 8003

# Orchestrator
cd services/orchestrator
uvicorn main:app --reload --port 8000
```

### 3 Run Frontend Apps
```bash
# Client Portal
cd ui/client-portal
npm install && npm start

# Driver App
cd ui/driver-app
npm install && npm start

```

Access:
- Client Portal → http://localhost:3000
- Driver App → http://localhost:3001

### 4 Testing the Flow

1. Submit a mock order via POST /orders (CMS Adapter).
1. Watch orchestration:
    - Order event appears in RabbitMQ queue.
    - ROS Integration fetches route → sends back to orchestrator.
   - WMS Adapter publishes package updates.
1. Client Portal updates order status automatically.
1. Driver App receives route & allows marking delivery as complete.
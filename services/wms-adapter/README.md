# WMS Adapter Service

## Overview
This service acts as an adapter between the Warehouse Management System (WMS) and other middleware components. It normalizes incoming data, publishes events, and communicates with the WMS server (mocked for local development).

## Prerequisites
- Python 3.11+
- Install dependencies:
  ```bash
  pip install -r requirements.txt
  ```

## How to Run
Run the mock WMS server in a separate terminal:
```bash
python wms_server_mock.py
```
Run the main adapter service:
```bash
python main.py
```

### With docker

Run the mock WMS server in the host machine:
```bash
python wms_server_mock.py
```
Build the Docker image:
```bash
docker build -t wms-adapter .
```
Run the Docker container:
```bash
docker run --name wms-adapter \
 --env-file ./app/.env \
 -d -p 8003:8003 \
 wms-adapter
```

## Debug Mode
To enable debug logging, set the environment variable before running:
`WMS_DEBUG=1`

- This will run the publisher as a standalone component, without waiting for other services.

## What Happens
- The adapter connects to the WMS server (see `wms_server_mock.py` for local testing).
- Incoming data is normalized using `normalizer.py`.
- Events are published via `publisher.py`.
- The service logs key actions and errors.

## File Structure
```
app/
  main.py                # Entry point for the adapter service
  requirements.txt       # Python dependencies
  wms_server_mock.py     # Mock WMS server for local development
  queue/
    client.py            # Handles WMS server communication
    consumer.py          # Message consumer logic
    publisher.py         # Event publishing logic
    rabbitmq_conn.py     # RabbitMQ connection setup
  wms/
    client.py            # WMS client logic
    mssg_parser.py       # Message parsing logic
  __init__.py            # Package initialization
  test/
    test_client.py       # Unit tests for WMS client
```
Dockerfile               # Container build file
README.md                # Service documentation

## Notes

The WMS server is mocked for local development. 
Docker listens to a mock WMS server running on the host machine at `http://host.docker.internal:9090` over TCP.

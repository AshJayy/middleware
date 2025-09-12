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

## Debug Mode
To enable debug logging, set the environment variable before running:
```bash
export DEBUG=1
python main.py
```
This will run the publisher as a standalone component, without waiting for other services.

## What Happens
- The adapter connects to the WMS server (see `wms_server_mock.py` for local testing).
- Incoming data is normalized using `normalizer.py`.
- Events are published via `publisher.py`.
- The service logs key actions and errors.

## File Structure
- `main.py`: Entry point for the service
- `client.py`: Handles WMS server communication
- `normalizer.py`: Data normalization logic
- `publisher.py`: Event publishing
- `wms_server_mock.py`: Mock WMS server for local development
- `test/`: Unit tests


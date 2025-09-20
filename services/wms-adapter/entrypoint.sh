#!/bin/bash

# Start the WMS mock server in the background
echo "[ENTRYPOINT] Starting WMS mock server..."
python wms_server_mock.py &

# Wait a moment for the mock server to start
sleep 2

# Start the main WMS adapter application
echo "[ENTRYPOINT] Starting WMS adapter service..."
exec uvicorn main:app --host 0.0.0.0 --port 8003

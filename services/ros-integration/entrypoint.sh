#!/bin/bash

# Start the WMS mock server in the background
echo "[ENTRYPOINT] Starting ROS mock server..."
python ros_server_mock.py &

# Wait a moment for the mock server to start
sleep 2

# Start the main WMS adapter application
echo "[ENTRYPOINT] Starting ROS adapter service..."
exec uvicorn main:app --host 0.0.0.0 --port 8002

#!/usr/bin/env bash
set -euo pipefail

# Configurable ports/hosts
ROS_HOST="${ROS_HOST:-0.0.0.0}"
ROS_PORT="${ROS_PORT:-3001}"
ADAPTER_HOST="${ADAPTER_HOST:-0.0.0.0}"
ADAPTER_PORT="${ADAPTER_PORT:-8002}"

# Point the consumer to the in-container mock
export ROS_API_URL="${ROS_API_URL:-http://127.0.0.1:${ROS_PORT}/optimize-route}"

echo "[ENTRYPOINT] Starting ROS mock server on ${ROS_HOST}:${ROS_PORT} ..."
uvicorn ros_server_mock:app --host "${ROS_HOST}" --port "${ROS_PORT}" --log-level info &
ROS_PID=$!
trap 'echo "[ENTRYPOINT] Shutting down..."; kill -TERM "${ROS_PID}" 2>/dev/null || true' EXIT

# Simple readiness wait for the mock
for _ in $(seq 1 20); do
  if python - <<'PY'
import socket, os, sys
s = socket.socket()
try:
    s.settimeout(0.5)
    s.connect(("127.0.0.1", int(os.environ.get("ROS_PORT","3001"))))
    sys.exit(0)
except Exception:
    sys.exit(1)
finally:
    s.close()
PY
  then
    break
  else
    sleep 0.5
  fi
done

echo "[ENTRYPOINT] Starting ROS adapter service on ${ADAPTER_HOST}:${ADAPTER_PORT} ..."
exec uvicorn main:app --host "${ADAPTER_HOST}" --port "${ADAPTER_PORT}" --log-level info
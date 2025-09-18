import asyncio
import random
from fastapi import FastAPI
from pydantic import BaseModel
from typing import List

app = FastAPI(title="Mock Route Optimization System")

# Pydantic models to validate incoming request data, ensuring a clear contract.
class Address(BaseModel):
    fullAddress: str
    city: str
    postalCode: str

class RouteRequest(BaseModel):
    orderId: str
    vehicleId: str
    pickup: Address  
    delivery: Address

@app.post("/optimize-route")
async def optimize_route(request: RouteRequest):
    """
    This endpoint simulates calculating an optimized route. It introduces
    an artificial delay to mimic a real-world network call, a key aspect
    of testing asynchronous communication.
    """
    print(f"✅ Mock ROS received request for Order ID: {request.orderId}")
    print(f"Routing from {request.pickup.fullAddress} to {request.delivery.fullAddress}")
    await asyncio.sleep(2)  # Simulate network latency

    # Generate a fake but realistic response, fulfilling the API contract.
    mock_route = {
        "routeId": f"route_{random.randint(1000, 9999)}",
        "orderId": request.orderId,
        "vehicleId": request.vehicleId,
        "status": "OPTIMIZED",
        "estimatedDurationMinutes": random.randint(30, 120),
        "pickupLocation": request.pickup.dict(),
        "deliveryLocation": request.delivery.dict()
    }
    print(f"✅ Mock ROS responding for Order ID: {request.orderId}")
    return mock_route

# --- How to run this mock server ---
# 3. Run the command: pip install "fastapi[all]"
# 4. Run the command: uvicorn ros_server_mock:app --reload --port 3001
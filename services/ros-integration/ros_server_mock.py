import asyncio
import random
from fastapi import FastAPI
from pydantic import BaseModel
from typing import List

app = FastAPI(title="Mock Route Optimization System")

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
    print(f"Mock ROS received request for Order ID: {request.orderId}")
    print(f"Routing from {request.pickup.city} to {request.delivery.city}")
    await asyncio.sleep(1) # Reduced delay for faster testing

    # --- MODIFIED RESPONSE PAYLOAD ---
    mock_route = {
        "routeId": f"route_{random.randint(1000, 9999)}",
        "orderId": request.orderId,
        "vehicleId": request.vehicleId,
        "status": "OPTIMIZED",
        # New 'waypoints' field with mock data
        "waypoints": [
            request.pickup.city,
            "Kottawa",
            "Kadawatha",
            request.delivery.city
        ]
    }
    print(f"Mock ROS responding for Order ID: {request.orderId}")
    return mock_route
import pika
import json
import asyncio
import httpx
from motor.motor_asyncio import AsyncIOMotorClient
import os

# --- Configuration ---
# Using environment variables for configuration is a best practice.
RABBITMQ_URI = os.getenv("RABBITMQ_URI", "amqp://guest:guest@rabbitmq:5672/")
MONGO_URI = os.getenv("MONGO_URI", "mongodb://root:example@mongo:27017/")
# This special URL points from inside a Docker container to your host machine.
ROS_API_URL = "http://host.docker.internal:3001/optimize-route"

# Queue names must match what the orchestrator uses.
INCOMING_QUEUE = "routing-queue"
OUTGOING_QUEUE = "order-updates-queue"

# --- Database Connection ---
client = AsyncIOMotorClient(MONGO_URI)
db = client.swiftlogistics
vehicles_collection = db.vehicles

async def start_consumer():
    """Connects to RabbitMQ and starts the main consumer loop."""
    print("Attempting to connect to RabbitMQ...")
    while True:
        try:
            connection = pika.BlockingConnection(pika.URLParameters(RABBITMQ_URI))
            channel = connection.channel()

            channel.queue_declare(queue=INCOMING_QUEUE, durable=True)
            channel.queue_declare(queue=OUTGOING_QUEUE, durable=True)
            print(f"✅ RabbitMQ connected. Waiting for messages in '{INCOMING_QUEUE}'")

            for method_frame, properties, body in channel.consume(INCOMING_QUEUE):
                try:
                    await process_message(channel, method_frame, body)
                except Exception as e:
                    print(f"❌ Error processing message: {e}")
                    channel.basic_nack(delivery_tag=method_frame.delivery_tag, requeue=False)
        
        except pika.exceptions.AMQPConnectionError:
            print("Connection to RabbitMQ failed. Retrying in 5 seconds...")
            await asyncio.sleep(5)

async def process_message(channel, method_frame, body):
    """Main logic for processing a single order message."""
    order_data = json.loads(body)
    order_id = order_data.get("orderId")
    print(f"[📥] Received routing request for Order ID: {order_id}")

    # 1. Find an available vehicle from MongoDB.
    vehicle = await vehicles_collection.find_one({"isAvailable": True})
    if not vehicle:
        raise Exception(f"No available vehicles found for order {order_id}")
    
    vehicle_id = vehicle["vehicleId"]
    print(f"[🚚] Found available vehicle: {vehicle_id}")

    # 2. Call the ROS API (our mock). This demonstrates integrating heterogeneous systems (REST API).
    ros_payload = {
        "orderId": order_id,
        "vehicleId": vehicle_id,
        "pickup": {
            "fullAddress": "SwiftLogistics Central Warehouse", # fixed pickup point
            "city": "Colombo",
            "postalCode": "00500"
        },
        "delivery": {
            "fullAddress": order_data.get("deliveryAddress"),
            "city": order_data.get("city"),
            "postalCode": order_data.get("postalCode")
        }
    }

    async with httpx.AsyncClient() as client:
        response = await client.post(ROS_API_URL, json=ros_payload, timeout=15.0)
        response.raise_for_status() # Raise exception for non-200 responses
        route_data = response.json()

    # 3. Publish the result back to the orchestrator.
    result_payload = json.dumps({
        "orderId": order_id,
        "status": "ROUTE_OPTIMIZED", # Status the orchestrator expects
        "data": route_data
    })
    channel.basic_publish(exchange='', routing_key=OUTGOING_QUEUE, body=result_payload)
    print(f"[📤] Published routing result for Order ID: {order_id}")

    # 4. Acknowledge the message was processed successfully.
    channel.basic_ack(delivery_tag=method_frame.delivery_tag)
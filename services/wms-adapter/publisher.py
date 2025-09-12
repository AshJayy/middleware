import json

import pika, os

def publish_to_queue(queue: str, message: dir):
    """Publish a message to the specified RabbitMQ queue"""

    if os.getenv("WMS_DEBUG") == "true":
        publish_to_queue_mock(queue, message)
        return

    connection = pika.BlockingConnection(
        pika.ConnectionParameters(
            host=os.environ['RABBITMQ_HOST'],
            port=int(os.environ['RABBITMQ_PORT']),
            credentials=pika.PlainCredentials(
                os.environ['RABBITMQ_USER'],
                os.environ['RABBITMQ_PASS']
            )
        )
    )

    channel = connection.channel()
    channel.queue_declare(queue=queue, durable=True)
    channel.basic_publish(
        exchange='',
        routing_key=queue,
        body=json.dumps(message),
    )

    print(f"Message published to queue '{queue}' : {message}")
    connection.close()

def publish_to_queue_mock(queue: str, message: dict):
    print(f"[SIMULATED PUBLISHER] Would send to {queue}: {message}")

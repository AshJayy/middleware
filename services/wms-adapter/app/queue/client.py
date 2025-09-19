# adapters/rabbitmq_client.py
import pika, json, os


def publish_mock(queue, message):
    print(f"[SIMULATED PUBLISHER] Would send to {queue}: {message}")


def consume_mock(queue, callback):
    sample_messages = [
        {"orderId": "ORD123", "status": "SHIPPED"},
        {"orderId": "ORD124", "status": "DELIVERED"},
        {"orderId": "ORD125", "status": "CANCELLED"},
        "close"
    ]
    for msg in sample_messages:
        print(f"[SIMULATED CONSUMER] Processing message from {queue}: {msg}")
        callback(msg)


class RabbitMQClient:
    def __init__(self):
        print("[RABBITMQ] Initializing RabbitMQ client...")
        self.connection = pika.BlockingConnection(
            pika.ConnectionParameters(
                host=os.getenv("RABBITMQ_HOST", "localhost"),
                port=int(os.getenv("RABBITMQ_PORT", 5672)),
                credentials=pika.PlainCredentials(
                    os.getenv("RABBITMQ_USER", "guest"),
                    os.getenv("RABBITMQ_PASS", "guest")
                )
            )
        )
        self.channel = self.connection.channel()


    def publish(self, queue, message):
        self.channel.queue_declare(queue=queue, durable=True)
        self.channel.basic_publish(
            exchange="",
            routing_key=queue,
            body=json.dumps(message),
            properties=pika.BasicProperties(delivery_mode=2)
        )
        print(f"[RABBITMQ] Published to {queue}: {message}")

    def consume(self, queue, callback):
        self.channel.queue_declare(queue=queue, durable=True)

        def on_message(ch, method, properties, body):
            callback(json.loads(body.decode()))
            ch.basic_ack(delivery_tag=method.delivery_tag)

        self.channel.basic_consume(queue=queue, on_message_callback=on_message)
        print(f"[RABBITMQ] Listening on {queue}...")
        self.channel.start_consuming()

    def close(self):
        self.connection.close()

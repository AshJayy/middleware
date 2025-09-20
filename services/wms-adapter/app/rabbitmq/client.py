import threading
import pika, json, os


class RabbitMQClient:
    def __init__(self):
        print("[RABBITMQ] Initializing RabbitMQ client...")
        self.lock = threading.Lock()
        self.connection = pika.BlockingConnection(
            pika.ConnectionParameters(
                host=os.getenv("RABBITMQ_HOST", "swift-rabbitmq"),
                port=int(os.getenv("RABBITMQ_PORT", 5672)),
                credentials=pika.PlainCredentials(
                    os.getenv("RABBITMQ_USER", "guest"),
                    os.getenv("RABBITMQ_PASS", "guest")
                )
            )
        )
        self.channel = self.connection.channel()

    def publish(self, queue, message):
        with self.lock:
            queue_args = {
                "x-dead-letter-exchange": os.getenv("DEAD_LETTER_EXCHANGE", "warehouse.exchange.dlx"),
                "x-dead-letter-routing-key": os.getenv("DEAD_LETTER_ROUTING_KEY", "warehouse-queue.dlq")
            }
            self.channel.queue_declare(queue=queue, durable=True, arguments=queue_args)
            self.channel.basic_publish(
                exchange="",
                routing_key=queue,
                body=json.dumps(message),
                properties=pika.BasicProperties(delivery_mode=2)
            )
            print(f"[RABBITMQ] Published to {queue}: {message}")

    def consume(self, queue, callback):
        with self.lock:
            queue_args = {
                "x-dead-letter-exchange": os.getenv("DEAD_LETTER_EXCHANGE", "warehouse.exchange.dlx"),
                "x-dead-letter-routing-key": os.getenv("DEAD_LETTER_ROUTING_KEY", "warehouse-queue.dlq")
            }
            self.channel.queue_declare(queue=queue, durable=True, arguments=queue_args)

            def on_message(ch, method, properties, body):
                callback(json.loads(body.decode()))
                ch.basic_ack(delivery_tag=method.delivery_tag)

            self.channel.basic_consume(queue=queue, on_message_callback=on_message)
            print(f"[RABBITMQ] Listening on {queue}...")
            self.channel.start_consuming()

    def close(self):
        with self.lock:
            self.connection.close()

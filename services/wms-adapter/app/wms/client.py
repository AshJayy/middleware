# wms_client.py
import socket, json, os

class WMSClient:
    def __init__(self):
        self.host = os.getenv("WMS_HOST", "localhost")
        self.port = int(os.getenv("WMS_PORT", 9000))
        self.sock = None
        self.connect()

    def connect(self):
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.connect((self.host, self.port))
        print(f"[WMS-ADAPTER] Connected to WMS at {self.host}:{self.port}")

    def send_order(self, order_data):
        try:
            message = json.dumps(order_data)
            self.sock.sendall(message.encode() + b"\n")
            print(f"[WMS-ADAPTER] Sent order {order_data[0]}")
        except (BrokenPipeError, ConnectionResetError):
            print("[WMS-ADAPTER] Lost connection. Reconnecting...")
            self.connect()
            self.send_order(order_data)

    def listen_for_updates(self, callback):
        while True:
            data = self.sock.recv(1024)
            if not data:
                break
            for line in data.decode("utf-8").strip().splitlines():
                callback(line)
        print("[WMS] Disconnected")
        self.sock.close()
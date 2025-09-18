# mock_wms_server.py
import socket
import threading
import time

HOST = "localhost"
PORT = 9000

orders_received = []

def handle_client(conn, addr):
    print(f"[MOCK-WMS] Connected by {addr}")
    with conn:
        while True:
            try:
                data = conn.recv(1024)

                # TODO: Implement a proper shutdown mechanism
                # if data.decode().strip() == "close":
                #     print("[MOCK-WMS] Client requested to close connection.")
                #     break

                if not data:
                    print("[MOCK-WMS] No data received. Closing connection.")
                    break

                for line in data.decode().splitlines():
                    line = line.strip()
                    if not line:
                        continue
                    # Expecting format: ORD345:SHIPPED
                    if ':' in line:
                        order_id, event = line.split(':', 1)
                        order_id = order_id.strip('"')
                        orders_received.append(line)
                        print(f"[MOCK-WMS] Received order: {line}")
                        # Simulate warehouse processing
                        threading.Thread(
                            target=simulate_processing,
                            args=(conn, order_id),
                            daemon=True
                        ).start()
                    else:
                        print("[MOCK-WMS] Invalid message received:", line)
            except ConnectionResetError:
                print("[MOCK-WMS] Client disconnected unexpectedly.")
                break


def simulate_processing(conn, order_id):
    # Simulate time taken to process
    time.sleep(3)
    package_ready = f"{order_id}:READY"
    conn.sendall((package_ready + "\n").encode())
    print(f"[MOCK-WMS] Sent PACKAGE_READY for order {order_id}")

    # Optional: simulate dispatch event after more time
    time.sleep(5)
    dispatched = f"{order_id}:DISPATCHED"
    conn.sendall((dispatched + "\n").encode())
    print(f"[MOCK-WMS] Sent PACKAGE_DISPATCHED for order {order_id}")


def start_server():
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        s.bind((HOST, PORT))
        s.listen()
        print(f"[MOCK-WMS] Server running on {HOST}:{PORT}")
        while True:
            conn, addr = s.accept()
            threading.Thread(target=handle_client, args=(conn, addr), daemon=True).start()


if __name__ == "__main__":
    start_server()

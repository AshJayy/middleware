# mock_wms_server.py
import socket
import threading
import time
import os

HOST = os.getenv("HOST", "0.0.0.0")
PORT = int(os.getenv("PORT", 9000))

orders_received = []


def handle_client(conn, addr):
    print(f"[MOCK-WMS] Connected by {addr}")
    with conn:
        while True:
            try:
                data = conn.recv(1024)
                if not data:
                    print("[MOCK-WMS] No data received. Closing connection.")
                    break

                for line in data.decode().splitlines():
                    line = line.strip()
                    if not line:
                        continue
                    if ':' in line:
                        order_id, event = line.split(':', 1)
                        order_id = order_id.strip('"')
                        orders_received.append(line)
                        print(f"[MOCK-WMS] Received order: {line}")
                        threading.Thread(
                            target=simulate_processing,
                            args=(conn, order_id, line),
                            daemon=True
                        ).start()
                    else:
                        print("[MOCK-WMS] Invalid message received:", line)
            except ConnectionResetError:
                print("[MOCK-WMS] Client disconnected unexpectedly.")
                break


def simulate_processing(conn, order_id, original_line):
    time.sleep(3)
    messages = [f"{order_id}:READY", original_line]
    for msg in messages:
        try:
            conn.sendall((msg + "\n").encode())
            print(f"[MOCK-WMS] Sent: {msg}")
        except (BrokenPipeError, OSError):
            print("[MOCK-WMS] Failed to send data back. Connection might be closed.")
            break


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

import socket

def listen(host: str, port: int, callback):
    """Listen for incoming connections on the specified host and port."""
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect((host, port))

    while True:
        data = s.recv(1024)
        if not data:
            break
        for line in data.decode("utf-8").strip().splitlines():
            callback(line)

    s.close()
    print("[WMS] Disconnected")
import socket, time

server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server.bind(("localhost", 9090))
server.listen(1)
print("[Mock WMS] Listening...")
conn, addr = server.accept()
print(f"[Mock WMS] Connected: {addr}")

for msg in ["ORD123:PACKAGE_RECEIVED", "ORD123:PACKAGE_LOADED", "ORD123:DELIVERED"]:
    time.sleep(2)
    conn.sendall((msg + "\n").encode())
    print(f"[Mock WMS] Sent: {msg}")

conn.close()
server.close()

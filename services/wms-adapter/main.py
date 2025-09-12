import os
import threading

from dotenv import load_dotenv
from fastapi import FastAPI

from normalizer import normalize_mssg
from publisher import publish_to_queue
from client import listen

load_dotenv()

app = FastAPI()


@app.get("/health")
def health():
    return {"status": "ok", "service": "WMS Adapter"}

def handle_message(message):
    normalized_mssg = normalize_mssg(message)
    publish_to_queue("wms", normalized_mssg)

@app.on_event("startup")
def start_wms_listener():
    host = os.getenv("WMS_TCP_HOST")
    port = int(os.getenv("WMS_TCP_PORT"))
    t = threading.Thread(target=listen , args=(host, port, handle_message))
    t.daemon = True
    t.start()

    # import uvicorn
    # uvicorn.run(app, host="0.0.0.0", port=8003)
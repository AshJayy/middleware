def normalize_mssg(raw_msg):
    """
    Converts raw WMS string messages into structured JSON-like dict.
    Example: "ORD345:SHIPPED" -> {"orderId": "12345", "status": "SHIPPED"}
    """
    try:
        order_id, status = raw_msg.split(":")
        return {"orderId": order_id, "status": status}
    except ValueError:
        return {"event": "UNKNOWN", "raw": raw_msg}
import NETWORK from './network'

export const orderEndpoints = {
  async createOrder(data) {
    return await NETWORK.post('/orders', data)
  },
  async getOrders(customerId) {
    return await NETWORK.get(`/orders/${customerId}`)
  },
  async getDriver(driverId) {
    return await NETWORK.get(`/drivers/${driverId}`)
  },

  async trackOrder(orderId) {
    const url = `${NETWORK.baseURL}/orders/track/${orderId}`;
    if (typeof(EventSource) !== "undefined") {
      const source = new EventSource(url);
      source.onmessage = function(event) {
        console.log("Order update:", event.data);
        // Here you can dispatch an action or update state with the new order status
      };
      source.onerror = function(err) {
        console.error("EventSource failed:", err);
        source.close();
      };
      return source; // Return the EventSource instance to allow closing it later
    } else {
      console.error("Your browser does not support Server-Sent Events.");
      return null;
    }
  }
}
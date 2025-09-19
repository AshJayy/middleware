import NETWORK from './network'

export const orderEndpoints = {
  async createOrder(data) {
    return await NETWORK.post('/orders', data)
  },
  async getOrders(userId) {
    return await NETWORK.get(`/orders/${userId}`)
  },
  async getDriver(driverId) {
    return await NETWORK.get(`/drivers/${driverId}`)
  }
}
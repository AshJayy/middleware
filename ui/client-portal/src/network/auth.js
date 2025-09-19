import NETWORK from './network'

export const authEndpoints = {
  async signin(data) {
    return await NETWORK.post('/auth/signin', data)
  }
}
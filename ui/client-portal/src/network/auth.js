import NETWORK from './network'

export const authEndpoints = {
  async signin(data) {
    return await NETWORK.post('/auth/customer', data)
  },

  async signup(data) {
    // Mock signup: no network call, accept name, email, password and create a customer object
    const { name, email, password } = data;
    if (!name?.trim() || !email?.trim() || !password) {
      throw new Error('All fields are required');
    }
    if (!/\S+@\S+\.\S+/.test(email)) {
      throw new Error('Invalid email');
    }
    if (password.length < 6) {
      throw new Error('Password must be at least 6 characters');
    }
    const cust = {
      customerName: name.trim(),
      customerEmail: email.trim(),
      password: password,
    };
    return NETWORK.post('/auth/sign-up', cust);
  }
}
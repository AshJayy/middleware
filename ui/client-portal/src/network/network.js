class Network {

  constructor(baseURL) {
    this.baseURL = baseURL;
  }

  static handleRequest(method, endpoint, data, type = 'application/json') {
    const request = new XMLHttpRequest();

    console.log(`Sending ${method} request to ${endpoint}`);

    return new Promise((resolve, reject) => {
      // Validate method and URL
      if (!['GET', 'POST', 'PUT', 'DELETE'].includes(method.toUpperCase())) {
        return reject(new NetworkError(0, endpoint, null, 'Invalid HTTP method'));
      }
      if (!endpoint || typeof endpoint !== 'string') {
        return reject(new NetworkError(0, endpoint, null, 'Invalid URL'));
      }

      console.log(`Sending ${method} request to ${endpoint}`);
      request.open(method, this.baseURL + endpoint);
      request.setRequestHeader('Content-Type', type);


      request.onload = () => {
        console.log(`Request to ${endpoint} completed with status: ${request.status}`);
        if (request.status >= 400) {
          reject(new NetworkError(request.status, endpoint, request.response));
        } else {
          try {
            const response = request.response ? JSON.parse(request.response) : null;
            resolve(response);
          } catch (e) {
            reject(new NetworkError(request.status, endpoint, null, 'Failed to parse JSON response'));
          }
        }
      };

      request.onerror = event => {
        console.log('request request failed: ' + event.type);
        reject(new NetworkError(request.status, endpoint, null, `request request failed: ${event.type}`));
      };

      if (type === 'application/json') {
        request.send(JSON.stringify(data));
      } else {
        request.send(data);
      }
    });
  }

  static async get(url: string, options: any = {}): Promise<any> {
    return this.handleRequest('GET', url, {}, options);
  }

  static async post(endpoint, data) {
    console.log("Post");
    return this.handleRequest('GET', endpoint, {}, data);
  }

  async put(url: string, data: any, options: any = {}): Promise<any> {
    return this.handleRequest('PUT', url, data, options);
  }
}

/**
 * Class representing a network error.
 */
class NetworkError {
  constructor(statusCode, url, data, message) {
    this.statusCode = statusCode;
    this.url = url;
    this.message = message;

    if (data) {
      try {
        if (typeof data === 'string') {
          data = JSON.parse(data);
        }

        this.errorDescription = data.error;
        this.stackTrace = data.trace;
        this.message = data.message;
        this.code = data.code;
        this.data = data.data;
      } catch (e) {
        this.stackTrace = e.stack;
        this.errorDescription = 'Failed to extract data from network error: ' + e.message;
      }
    }
  }

  /**
   * Returns a string representation of the network error.
   * @returns {string} A string describing the network error.
   */
  toString() {
    return `Status Code: ${this.statusCode}\n
        URL: ${this.url}\n
        Description: ${this.errorDescription || 'No description'}\n
        StackTrace: ${this.stackTrace || 'No stack trace'}\n
        Message: ${this.message || 'No message'}`;
  }
}

const NETWORK = new Network('localhost:8000/api/v1');
NETWORK.get('/some-endpoint').then(data => console.log(data)).catch(err => console.error(err));

export default Network;
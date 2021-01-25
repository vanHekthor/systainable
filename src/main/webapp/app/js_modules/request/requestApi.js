import axios from 'axios';

const SERVER_URL = process.env.SERVER_API_URL;

const instance = axios.create({
  baseURL: SERVER_URL,
});

export default {
  /**
   * This method sends a GET request to /systems.
   * @returns {Promise<any>} String array with names of available systems
   */
  getAvailableSystems: async function getAvailabeSystems() {
    let response = await instance.get('systems').catch(error => console.log(error));

    return response.data;
  },

  /**
   * This method sends a GET request to /featuremodel?name=${systemName}.
   * @param systemName Name of the system
   * @returns {Promise<any>} Object with software system feature and property attributes
   */
  getAttributeNames: async function getAttributeNames(systemName) {
    let response = await instance.get(`featuremodel?name=${systemName}`).catch(error => console.log(error));

    return response.data;
  },

  /**
   * This method sends a GET request to /featuremodel/initconfig?name=${systemName}.
   * @param systemName Name of the system
   * @returns {Promise<any>} Valid minimal configuration in request format
   */
  getInitConfig: async function getConfig(systemName) {
    let response = await instance.get(`featuremodel/initconfig?name=${systemName}`).catch(error => console.log(error));

    return response.data;
  },

  /**
   * This method sends a POST request to /featuremodel/valid. The request body contains a configuration in request format.
   * @param featureConfiguration Configuration that is put into the request body.
   * @returns {Promise<any>} Validity of sent configuration true/false
   */
  getValidity: async function getValidity(featureConfiguration) {
    let response = await instance.post('featuremodel/valid', { featureConfiguration }).catch(error => console.log(error));
    return response.data;
  },

  /**
   * This method sends a POST request to /performance. The request body contains a configuration in request format.
   * @param featureConfiguration Configuration that is put into the request body
   * @returns {Promise<any>} Object with property values of sent configuration
   */
  getPropValues: async function getPropValues(featureConfiguration) {
    let response = await instance.post('performance', { featureConfiguration }).catch(error => console.log(error));
    return response.data;
  },

  /**
   * This method sends a POST request to /performance/optimum/local?featuresystem=${systemName}&property=${propertyName}. The request body contains a configuration in request format.
   * @param systemName Name of the system
   * @param propertyName Name of the property to be improved
   * @param featureConfiguration Configuration that is put into the request body and shall be optimized
   * @returns {Promise<any>} Object with optimized configuration or message similar to 'no better configuration found'
   */
  getOptimizedConfig: async function getOptimizedConfig(systemName, propertyName, featureConfiguration) {
    let response = await instance
      .post(`performance/optimum/local?featuresystem=${systemName}&property=${propertyName}`, { featureConfiguration })
      .catch(error => console.log(error));
    return response.data;
  },
};

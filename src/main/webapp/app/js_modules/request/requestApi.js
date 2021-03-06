import axios from 'axios';

const SERVER_URL = process.env.SERVER_API_URL;

const instance = axios.create({
  baseURL: SERVER_URL,
});

export default {
  /**
   * This method sends a GET request to /systems/activateExamples.
   * @returns {Promise<any>} String array with names of available systems
   */
  activateExampleModels: async function () {
    let response = await instance.get('systems/activateExamples').catch(error => console.log(error));

    return response.data;
  },

  /**
   * This method sends a GET request to /systems.
   * @returns {Promise<any>} String array with names of available systems
   */
  getAvailableSystems: async function () {
    let response = await instance.get('systems').catch(error => console.log(error));

    return response.data;
  },

  /**
   * This method sends a GET request to /featuremodel?name=${systemName}.
   * @param systemName Name of the system
   * @returns {Promise<any>} Object with software system feature and property attributes
   */
  getAttributeNames: async function (systemName) {
    let response = await instance.get(`featuremodel?name=${systemName}`).catch(error => console.log(error));

    return response.data;
  },

  /**
   * This method sends a GET request to /featuremodel/initconfig?name=${systemName}.
   * @param systemName Name of the system
   * @returns {Promise<any>} Valid minimal configuration in request format
   */
  getInitConfig: async function (systemName) {
    let response = await instance.get(`featuremodel/initconfig?name=${systemName}`).catch(error => console.log(error));

    return response.data;
  },

  /**
   * This method sends a POST request to /featuremodel/valid. The request body contains a configuration in request format.
   * @param featureConfiguration Configuration that is put into the request body.
   * @returns {Promise<any>} Validity of sent configuration true/false
   */
  getValidity: async function (featureConfiguration) {
    let response = await instance.post('featuremodel/valid', { featureConfiguration }).catch(error => console.log(error));
    return response.data;
  },

  /**
   * This method sends a POST request to /performance. The request body contains a configuration in request format.
   * @param featureConfiguration Configuration that is put into the request body
   * @returns {Promise<any>} Object with property values of sent configuration
   */
  getPropValues: async function (featureConfiguration) {
    let response = await instance.post('performance', { featureConfiguration }).catch(error => console.log(error));
    return response.data;
  },

  getAlternativeConfig: async function (featureConfiguration) {
    let response = await instance.post('featuremodel/alternative', { featureConfiguration }).catch(error => console.log(error));
    return response.data;
  },

  /**
   * This method sends a POST request to /performance/optimum/local?property=${propName}&maxDifference=${maxDifference}.
   * The request body contains a configuration in request format.
   * @param propName Name of the property to be improved
   * @param maxDifference Max. difference of optimized configuration to sent configuration
   * @param featureConfiguration Configuration that is put into the request body and shall be optimized
   * @returns {Promise<any>} Object with optimized configuration or message similar to 'no better configuration found'
   */
  getOptimizedConfig: async function (propName, maxDifference, featureConfiguration) {
    let response = await instance
      .post(`performance/optimum/local?property=${propName}&maxDifference=${maxDifference}`, { featureConfiguration })
      .catch(error => console.log(error));
    return response.data;
  },

  /**
   * This method sends a GET request to /performance/optimum/global?featuresystem=${systemName}&property=${propName}.
   * @param systemName Selected software system
   * @param propName Selected property name for which near optimum is wished for.
   * @returns {Promise<any>} Near optimal configuration in request format
   */
  getNearOptimalConfig: async function (systemName, propName) {
    let response = await instance
      .get(`performance/optimum/global?featuresystem=${systemName}&property=${propName}`)
      .catch(error => console.log(error));
    return response.data;
  },
};

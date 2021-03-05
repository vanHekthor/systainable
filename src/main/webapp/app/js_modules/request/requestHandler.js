import api from './requestApi';
import underscoreUtil from '../util/underscoreUtil';

/**
 * This function converts the app-internal configuration format into the format needed for requests.
 * @param systemName name of the software systems this configuration belongs to
 * @param config configuration to be converted
 * @param properties properties belonging to the software system as well as the configuration
 * @returns {{featureModel: *, features: {}, properties: {}}} Configuration in request format
 */
function createRequestConfig(systemName, config, properties) {
  const requestConfig = {
    featureModel: systemName,
    features: {},
    properties: {},
  };

  Object.keys(config).forEach(function (key) {
    if (key !== 'name' && key !== 'properties' && key !== 'dissectedProperties') {
      requestConfig.features[key.replace(/\u00a0/g, '_')] = config[key];
    }
  });

  for (let prop of Object.keys(properties)) {
    requestConfig.properties[prop.replace(/\u00a0/g, '_')] = 1.1;
  }

  return requestConfig;
}

function convertResponse(responseData) {
  responseData.features = underscoreUtil.replaceUnderscores(responseData.features);
  responseData.properties = underscoreUtil.replaceUnderscores(responseData.properties);
  let config = Object.assign({ name: 'config' }, responseData.features);
  config.properties = responseData.properties;

  return config;
}

export default {
  /**
   * This method request the backend to load the available systems.
   * @returns {Promise<*>} String array with names of available systems
   */
  activateExampleModels: async function () {
    let responseData = await api.activateExampleModels();
    return responseData.systemNames;
  },

  /**
   * This method makes requests for available software systems.
   * @returns {Promise<*>} String array with names of available systems
   */
  getAvailableSystems: async function () {
    let responseData = await api.getAvailableSystems();
    return responseData.systemNames;
  },

  /**
   * This method requests the feature/option names of a selected software system.<br>
   * Additional content of response (feature attributes):
   * - binary/numeric features<br>
   * - numeric feature constraints<br>
   * - numeric feature step-function<br>
   * Underscores get replaced with non-break spaces.
   * @param systemName Selected software system
   * @returns {Promise<{}|undefined>} Object with feature attributes
   */
  getFeatureNames: async function (systemName) {
    let responseData = await api.getAttributeNames(systemName);

    await Object.keys(responseData.features).forEach(function (key) {
      responseData.features[key] = underscoreUtil.replaceUnderscores(responseData.features[key]);
    });

    return responseData.features;
  },

  /**
   * This method requests the property names of a selected software system with additional information about property units and remarks about desirable values
   * (lower/higher? == better). Underscores get replaced with non-break spaces.
   * @param systemName Selected software system
   * @returns {Promise<{}|undefined>} Object with property names as keys and units plus desirable values (lower/higher)
   */
  getPropNames: async function (systemName) {
    let responseData = await api.getAttributeNames(systemName);
    responseData.properties = underscoreUtil.replaceUnderscores(responseData.properties);

    return responseData.properties;
  },

  /**
   * This method requests an initial valid minimal configuration of a selected software system. The received configuration gets adapted to the app-internal format by
   * adding a name property. Underscores in object keys get replaced with non-break spaces.
   * @param systemName Selected software system
   * @returns {Promise<{name: string}|undefined>} Valid minimal configuration
   */
  getInitConfig: async function (systemName) {
    let responseData = await api.getInitConfig(systemName);

    responseData.features = underscoreUtil.replaceUnderscores(responseData.features);
    responseData.properties = underscoreUtil.replaceUnderscores(responseData.properties);

    return Object.assign({ name: 'config' }, responseData.features);
  },

  /**
   * This method requests if a configuration is valid.
   * @param systemName Selected software system
   * @param config Configuration to be checked
   * @param properties Configuration properties object
   * @returns {Promise<*>} Validity of the configuration true/false
   */
  validateConfig: async function (systemName, config, properties) {
    const requestConfig = createRequestConfig(systemName, config, properties);
    return await api.getValidity(requestConfig);
  },

  /**
   * This method requests the evaluation of a configuration. Underscores in response config attribute names get replaced by non-break spaces.
   * @param systemName Selected software system
   * @param config Configuration to be evaluated
   * @param properties Configuration properties object
   * @returns {Promise<{}|undefined>} Fully evaluated configuration object
   */
  getEvaluatedConfig: async function (systemName, config, properties) {
    const requestConfig = createRequestConfig(systemName, config, properties);

    let responseData = await api.getPropValues(requestConfig);
    responseData.features = underscoreUtil.replaceUnderscores(responseData.features);
    responseData.properties = underscoreUtil.replaceUnderscores(responseData.properties);

    let evaluatedConfig = Object.assign({ name: 'config' }, responseData.features);
    evaluatedConfig.properties = responseData.properties;
    evaluatedConfig.dissectedProperties = responseData.dissectedProperties;

    return evaluatedConfig;
  },

  getAlternativeConfig: async function (systemName, config, properties) {
    const requestConfig = createRequestConfig(systemName, config, properties);

    let responseData = await api.getAlternativeConfig(requestConfig);
    responseData.features = underscoreUtil.replaceUnderscores(responseData.features);

    const altConfig = Object.assign({ name: 'config' }, responseData.features);

    return altConfig;
  },

  /**
   * This method requests the optimization of a configuration given a selected property and a max. difference of configuration features/options.
   * Underscores in response object keys get replaced by non-break spaces.
   * @param systemName Selected software system
   * @param propName Selected property name
   * @param maxDifference Max. feature/option difference to original configuration
   * @param config Configuration to be optimized (alias original configuration)
   * @param properties Configuration properties object
   * @returns {Promise<(string)|({name: string} & Array)|({name: string} & Object)>}
   */
  getOptimizedConfig: async function (systemName, propName, maxDifference, config, properties) {
    const requestConfig = createRequestConfig(systemName, config, properties);

    let responseData = await api.getOptimizedConfig(propName.replace(/\u00a0/g, '_'), maxDifference, requestConfig);

    if (responseData === '') {
      return responseData;
    } else {
      return convertResponse(responseData);
    }
  },

  getNearOptimalConfig: async function (systemName, propName) {
    let responseData = await api.getNearOptimalConfig(systemName, propName.replace(/\u00a0/g, '_'));

    return convertResponse(responseData);
  },
};

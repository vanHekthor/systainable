import api from './Api';

function createRequestConfig(systemName, config, propNames) {
  const requestConfig = {
    featureModel: systemName,
    features: {},
    properties: {},
  };

  Object.keys(config).forEach(function (key) {
    if (key !== 'name') {
      requestConfig.features[key] = config[key];
    }
  });

  for (let prop of propNames) {
    requestConfig.properties[prop] = 1.1;
  }

  return requestConfig;
}

export default {
  getAvailableSystems: async function getAvailableSystems() {
    let responseData = await api.getAvailableSystems();

    return responseData.systemNames;
  },

  getFeatureNames: async function getFeatureNames(systemName) {
    let responseData = await api.getAttributeNames(systemName);

    return responseData.features;
  },

  getPropNames: async function getPropNames(systemName) {
    let responseData = await api.getAttributeNames(systemName);

    return Object.keys(responseData.properties);
  },

  getInitConfig: async function getInitConfig(systemName) {
    let responseData = await api.getInitConfig(systemName);

    return Object.assign({ name: 'config' }, responseData.featureConfiguration.features);
  },

  validateConfig: async function validate(systemName, config, propNames) {
    const requestConfig = createRequestConfig(systemName, config, propNames);
    return await api.getValidity(requestConfig);
  },

  getConfigPropValues: async function getConfigPropValues(systemName, config, propNames) {
    const requestConfig = createRequestConfig(systemName, config, propNames);
    let responseData = await api.getPropValues(requestConfig);

    return responseData.featureConfiguration.properties;
  },
};

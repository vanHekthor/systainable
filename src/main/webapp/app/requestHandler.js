import api from './Api';

export default {
  getFeatureNames: async function getFeatureNames() {
    let responseData = await api.getFeatureNames();

    // compare this output to output console.log(response.data.feature) in Api.js
    console.log(responseData);

    for (let feature of responseData.featureModel.feature) {
      console.log(feature);
    }

    return responseData.featureModel.feature;
  },

  getConfig: async function getConfig() {
    let responseData = await api.getFeatureConfig();
    var features = responseData.featureModel.features;
    let featList = {};

    for (var i = 0; i < features.length; i++) {
      let valFeatures = features[i];
      featList[valFeatures] = true;
      /*
        let valFeatures = { [features[i]]: true };
        featList.push(valFeatures);*/
    }

    let featureName = { name: 'config' };
    const exampleConfig = Object.assign(featureName, featList);

    return exampleConfig;
  },

  getConfigProperties: async function getConfigProperties() {
    let responseData = await api.getProp();

    // compare this output to output console.log(response.data.feature) in Api.js
    console.log(responseData);

    for (let property of responseData.featureModel.properties) {
      console.log(property);
    }

    return responseData.featureModel.properties;
  },
};

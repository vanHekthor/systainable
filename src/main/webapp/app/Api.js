import axios from 'axios';

const SERVER_URL = process.env.VUE_APP_API_SERVER_URL;

const instance = axios.create({
  baseURL: SERVER_URL,
});

export default {
  getAvailableSystems: async function getAvailabeSystems() {
    let response = await instance.get('systems').catch(error => console.log(error));

    return response.data;
  },

  getAttributeNames: async function getAttributeNames(systemName) {
    let response = await instance.get(`featuremodel?name=${systemName}`).catch(error => console.log(error));

    return response.data;
  },

  getInitConfig: async function getConfig(systemName) {
    let exampleInitConfig = null;

    // dummy data
    if (systemName === 'providedModel') {
      exampleInitConfig = {
        features: {
          root: true,
          compressed_script: false,
          encryption: false,
          crypt_aes: false,
          crypt_blowfish: false,
          transaction_control: true,
          txc_mvlocks: true,
          txc_mvcc: false,
          txc_locks: false,
          table_type: true,
          memory_tables: true,
          cached_tables: false,
          small_cache: false,
          large_cache: false,
          logging: false,
          detailed_logging: false,
          no_write_delay: false,
          small_log: false,
        },
      };
    } else if (systemName === 'customModel1') {
      exampleInitConfig = {
        features: {
          Auto: true,
          Heckspoiler: false,
          Motor: true,
          Benzin: true,
          Diesel: false,
          Wasserstoff: false,
          Tuning: false,
          Turbolader: false,
          'AnhÃ¤ngerkupplung': false,
          Elektro: false,
        },
      };
    }

    let response = await instance.get(`featuremodel/initconfig?name=${systemName}`).catch(error => console.log(error));

    return response.data;
  },

  getValidity: async function getValidity(featureConfiguration) {
    let response = await instance.post('featuremodel/valid', { featureConfiguration }).catch(error => console.log(error));
    return response.data;
  },

  getPropValues: async function getPropValues(featureConfiguration) {
    let response = await instance.post('performance', { featureConfiguration }).catch(error => console.log(error));
    return response.data;
  },

  getOptimizedConfig: async function getOptimizedConfig(systemName, propertyName, featureConfiguration) {
    let response = await instance
      .post(`performance/optimum/local?featuresystem=${systemName}&property=${propertyName}`, { featureConfiguration })
      .catch(error => console.log(error));
    return response.data;
  },

  // the following methods generate dummy data for frontend dev without backend responses
  getFeatureNamesExample: function getFeatureNamesExample() {
    // dummy data
    let featureNames = [
      'name',
      'root',
      'compressed_script',
      'encryption',
      'crypt_aes',
      'crypt_blowfish',
      'transaction_control',
      'txc_mvlocks',
      'txc_nvcc',
      'txc_locks',
      'table_type',
      'memory_tables',
      'cached_tables',
      'small_cache',
      'large_cache',
      'logging',
      'detailed_logging',
      'no_write_delay',
      'small_log',
    ];
    return featureNames;
  },

  getConfigExample: function getConfigExample() {
    // dummy data
    let exampleConfig = {
      name: 'config',
      root: true,
      compressed_script: true,
      encryption: false,
      crypt_aes: false,
      crypt_blowfish: false,
      transaction_control: true,
      txc_mvlocks: true,
      txc_nvcc: true,
      txc_locks: true,
      table_type: false,
      memory_tables: false,
      cached_tables: false,
      small_cache: false,
      large_cache: false,
      logging: true,
      detailed_logging: true,
      no_write_delay: true,
      small_log: false,
      files: 10,
      blockSize: 16,
      x: 4,
    };

    return exampleConfig;
  },

  getPropertiesExample: function getPropertiesExample() {
    // dummy data
    let energy = Math.random() * 100;
    let time = Math.random() * 1000;
    let cpu = Math.random() * 10;
    let examplePropertyValues = new Map();
    examplePropertyValues.set('energy', energy);
    examplePropertyValues.set('time', time);
    examplePropertyValues.set('cpu', cpu);

    return examplePropertyValues;
  },
};

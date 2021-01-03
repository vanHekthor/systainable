import axios from 'axios';

const SERVER_URL = process.env.VUE_APP_API_SERVER_URL;

const instance = axios.create({
  baseURL: SERVER_URL,
});

export default {
  // GET
  getFeatureNamesExample: function getFeatureNamesExample() {
    instance.get('featuremodels/example/featuremodel1').then(response => {
      console.log(response.data);
      console.log(response.status);
      console.log(response.statusText);
      console.log(response.headers);
      console.log(response.config);
    });

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
    instance.get('configexample').then(response => {
      console.log(response.data);
      console.log(response.status);
      console.log(response.statusText);
      console.log(response.headers);
      console.log(response.config);
    });

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
    };

    return exampleConfig;
  },

  getValidity: function getValidity(config) {
    if (config.encryption && config.crypt_aes && config.crypt_blowfish) {
      return false;
    }
    if ((!config.encryption && config.crypt_aes) || (!config.encryption && config.crypt_blowfish)) {
      return false;
    }
    if (config.encryption && !config.crypt_aes && !config.crypt_blowfish) {
      return false;
    }
    return true;
  },

  getProperties: function getProperties(config) {
    instance.get('property-prediction', config).then(response => {
      console.log(response.data);
      console.log(response.status);
      console.log(response.statusText);
      console.log(response.headers);
      console.log(response.config);
    });

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

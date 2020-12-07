<template id="app">
  <ConfigurationTable
      v-bind:configurationFeatures="configurationFeatures"
      v-bind:configurations="configurations" v-on:update-feature="updateFeature"/>
  <button @click="getFeatures">load features</button>
  <button @click="getConfigExample">add example config</button>
</template>

<script>
import api from './Api';
import ConfigurationTable from './components/ConfigurationTable';


export default {
  name: 'App',
  components: {
    ConfigurationTable
  },
  data() {
    return {
      featureModel: "",
      configurationFeatures: [
        {
          name: "Feature 1"
        },
        {
          name: "Feature 2"
        },
        {
          name: "Feature 3"
        },
        {
          name: "Feature 4"
        },
        {
          name: "Feature 5"
        },
        {
          name: "Feature 6"
        },
        {
          name: "Feature 7"
        },
        {
          name: "Feature 8"
        },
        {
          name: "Feature 9"
        },
        {
          name: "Feature 10"
        },
        {
          name: "Feature 11"
        },
        {
          name: "Feature 12"
        }
      ],
      configurationProperties: [],
      configurations: [
      ]
    }
  },
  methods: {
    //turns feature in the cell(configIndex, featureIndex) on/off
    updateFeature(configIndex, featureIndex) {
      this.configurations[configIndex].features[featureIndex]
          = !this.configurations[configIndex].features[featureIndex];
    },

    //requests features from backend
    getFeatures() {
      //JSON-Parser is still WIP
      let featureNames = [
        "root", "compressed_script", "encryption", "crypt_aes", "crypt_blowfish",
        "transaction_control", "txc_mvlocks", "txc_nvcc", "txc_locks", "table_type", "memory_tables",
        "cached_tables", "small_cache", "large_cache", "logging", "detailed_logging", "no_write_delay",
        "small_log"
      ];

      //request itself works
      try {
        api.getFeatures();
        this.updateFeatureNames(featureNames);
      } catch (error) {
        this.$log.debug(error)
        this.error = "Failed to load features";
      }finally {
        this.loading = false;
      }
    },

    updateFeatureNames(featureNames) {
      let names = [];
      let featureName;
      for (featureName of featureNames) {
        names.push({name: featureName});
      }
      this.configurationFeatures = names;
    },

    getConfigExample() {
      let exampleConfig = {
        name: "example",
        features: {
          root: true, compressed_script: true, encryption:false, crypt_aes: false,crypt_blowfish: false,
          transaction_control: true, txc_mvlocks: true, txc_nvcc: true, txc_locks: true, table_type:false,
          memory_tables:false, cached_tables: false, small_cache: false, large_cache: false, logging: true,
          detailed_logging: true, no_write_delay: true, small_log:false
        }
      };

      try {
        this.featureModel = api.getConfigExample();
        this.addConfiguration(exampleConfig);
      } catch (error) {
        this.$log.debug(error)
        this.error = "Failed to load config example";
      }finally {
        this.loading = false;
      }
    },

    addConfiguration(config) {
      this.configurations.push(config);
    }


  }

}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>

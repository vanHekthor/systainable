<template id="app">
  <Toolbar class="p-mb-4">
    <template #left>
      <Dropdown v-model="selectedSoftSystem" :options="softSystems" optionLabel="name" placeholder="Select a system"
                @change="getFeatures"/>
    </template>
  </Toolbar>

  <ConfigTable
    :configurationFeatures="configurationFeatures"
    :configurations="configurations"
    v-on:update-feature="updateFeature"
    v-on:update-config-name="updateConfigName"
    v-on:del-config="deleteConfig"
    class ="p-mb-4"/>

  <Toolbar class="p-mb-4">
    <template #left>
      <Button icon="pi pi-plus" class="p-button-sm" @click="getConfigExample"/>
    </template>
    <template #right>
      <Button label="Submit" class="p-button-sm p-mr-2" @click="submitConfig"/>
    </template>
  </Toolbar>

  <ChartArea v-if="draw"/>
</template>

<script>
import api from './Api';
import ConfigTable from './components/ConfigTable';
import ChartArea from './components/ChartArea';
import Button from 'primevue/button';
import Toolbar from 'primevue/toolbar';
import Dropdown from 'primevue/dropdown';




export default {
  name: 'App',
  components: {
    ConfigTable,
    ChartArea,
    Button,
    Toolbar,
    Dropdown
  },
  data() {
    return {
      featureModel: "",
      configurationFeatures: [],
      configurationProperties: [],
      configurations: [],
      draw: false,
      selectedSoftSystem: null,
      softSystems: [
        {name: 'KrasseSoft'},
        {name: 'HyperSoft'},
        {name: 'Macrosoft'},
        {name: 'Peach'},
        {name: 'Sungsam'}
      ]
    }
  },

  methods: {
    //turns feature in the cell(configIndex, featureIndex) on/off
    updateFeature(index, featureName) {
      this.configurations[index][featureName]
          = !this.configurations[index][featureName];
    },

    updateConfigName(index, configName) {
      this.configurations[index].name = configName;
    },

    //requests features from backend
    getFeatures() {
      //JSON-Parser is still WIP
      let featureNames = [
        "name", "root", "compressed_script", "encryption", "crypt_aes", "crypt_blowfish",
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
      if (this.configurations.length == 0) {
        this.getConfigExample();
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
          name: "example", root: true, compressed_script: true, encryption:false, crypt_aes: false,
          crypt_blowfish: false, transaction_control: true, txc_mvlocks: true, txc_nvcc: true, txc_locks: true,
          table_type:false, memory_tables:false, cached_tables: false, small_cache: false, large_cache: false,
          logging: true, detailed_logging: true, no_write_delay: true, small_log:false
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
    },

    deleteConfig(index) {
      console.log("remove " + index);
      this.configurations.splice(index, 1);
    },

    submitConfig() {
      this.drawCharts();
    },

    drawCharts() {
      this.draw = true;
    }
  }

}
</script>

<style>
#app {
}
</style>

<template id="app">
  <Card class="p-shadow-1 p-m-0">
    <template #title>
      <p>Green Configurator</p>
    </template>
    <template #content>
      <div class="p-d-flex p-jc-center p-mb-4">
        <span class="p-float-label">
          <Dropdown id="selectDropdown" v-model="selectedSoftSystem" :options="softSystems" optionLabel="name"
                    @change="getFeatures"/>
          <label for="selectDropdown">Select a system</label>
        </span>
      </div>
      <ConfigTable
          :configurationFeatures="configurationFeatures"
          :configurations="configurations"
          v-on:update-feature="updateFeature"
          v-on:update-config-name="updateConfigName"
          v-on:del-config="deleteConfig"/>
    </template>
    <template #footer>
      <div class="p-d-flex p-jc-center">
        <Button class="p-button-sm p-mr-2" icon="pi pi-plus" @click="getConfigExample"/>
        <Button v-if="configurations.length < 1" class="p-button-sm p-mr-2" label="Submit" disabled="disabled"/>
        <Button v-else class="p-button-sm p-mr-2" label="Submit"  @click="submitConfig"/>
      </div>
    </template>
  </Card>

  <ChartArea v-if="draw & configurations.length > 0"/>
</template>

<script>
import api from './Api';
import ConfigTable from './components/ConfigTable';
import ChartArea from './components/ChartArea';
import Button from 'primevue/button';
import Dropdown from 'primevue/dropdown';
import Card from 'primevue/card';




export default {
  name: 'App',
  components: {
    ConfigTable,
    ChartArea,
    Button,
    Dropdown,
    Card
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
      if (this.configurations.length < 1) {
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

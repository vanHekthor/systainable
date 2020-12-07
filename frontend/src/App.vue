<template id="app">
  <ConfigurationTable
      v-bind:configurationFeatures="configurationFeatures"
      v-bind:configurations="configurations" v-on:update-feature="updateFeature"/>
  <button @click="getFeatures">get features</button>
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
        {
          name: "Config 1",
          features: [
            true, false, false,true, false, false, true, false, false, true, false, false
          ]
        },
        {
          name: "Config 2",
          features: [
            true, true, true,true, false, false, true, false, false,true, false, false
          ]
        },
        {
          name: "Config 3",
          features: [
            false, true, true,true, false, false,true, false, false, true, false, false
          ]
        }
      ]
    }
  },
  methods: {
    updateFeature(configIndex, featureIndex) {
      this.configurations[configIndex].features[featureIndex] = !this.configurations[configIndex].features[featureIndex];
    },
    getFeatures() {
      api.getFeatures()
          .catch(error => {
            this.$log.debug(error)
            this.error = "Failed to load features"
          })
          .finally(() => this.loading = false)
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

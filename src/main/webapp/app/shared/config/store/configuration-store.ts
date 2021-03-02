import Vue from 'vue';
import { Module } from 'vuex';
import { getField, updateField } from 'vuex-map-fields';

export const configurationStore: Module<any, any> = {
  namespaced: true,
  state: {
    softSystems: [],
    selectedSoftSystem: '',
    systemFeatures: {},
    systemProperties: {},
    configurations: [],
  },
  getters: {
    getField,
  },
  mutations: {
    updateField,
    addConfigToStore(state, config) {
      state.configurations.push(config);
    },
    deleteConfigFromStore(state, index) {
      state.configurations.splice(index, 1);
    },
    insertConfigToStore(state, { index, config }) {
      const insert = (arr, index, newItem) => [...arr.slice(0, index), newItem, ...arr.slice(index)];

      state.configurations = insert(state.configurations, index + 1, config);
    },
    updateConfigNameFromStore(state, { index, configName }) {
      state.configurations[index].name = configName;
    },
    updateConfigFeature(state, { index, featureName, value }) {
      state.configurations[index][featureName] = value;
    },
    setConfigProperties(state, { index, properties }) {
      Vue.set(state.configurations[index], 'properties', properties);
    },
  },
};

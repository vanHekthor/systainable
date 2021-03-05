import Vue from 'vue';
import { Module } from 'vuex';
import { getField, updateField } from 'vuex-map-fields';

export const uiLogicStore: Module<any, any> = {
  namespaced: true,
  state: {
    softSystemLoaded: false,
    selectedSoftSystem: '',
    previousSelection: '',
    showInfluences: false,
    chartsDrawn: false,
    visibleProperties: {},
  },
  getters: {
    getField,
  },
  mutations: {
    updateField,
    setPropertyVisibility: (state, { property, visible }) => {
      Vue.set(state.visibleProperties, property, visible);
      //state.visibleProperties[property] = visible;
    },
  },
};

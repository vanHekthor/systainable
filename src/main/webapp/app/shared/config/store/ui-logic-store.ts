import { Module } from 'vuex';
import { getField, updateField } from 'vuex-map-fields';

export const uiLogicStore: Module<any, any> = {
  namespaced: true,
  state: {
    softSystemLoaded: false,
    chartsDrawn: false,
  },
  getters: {
    getField,
  },
  mutations: {
    updateField,
  },
};

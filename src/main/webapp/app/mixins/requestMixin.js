import requestHandler from '../js_modules/request/requestHandler';
import { mapFields } from 'vuex-map-fields';
import { mapMutations } from 'vuex';

export default {
  computed: {
    ...mapFields(`configurationStore`, ['softSystems', 'configurations', 'systemFeatures', 'systemProperties']),

    ...mapFields(`uiLogicStore`, ['selectedSoftSystem']),
  },

  created: async function () {
    this.softSystems = await requestHandler.activateExampleModels();
  },

  methods: {
    ...mapMutations('configurationStore', ['setConfigProperties', 'setDissectedConfigProperties']),

    requestSystemAttributes: async function (event) {
      this.systemFeatures = await requestHandler.getFeatureNames(event.value);
      this.systemProperties = await requestHandler.getPropNames(event.value);
    },

    requestInitConfig: async function () {
      return await requestHandler.getInitConfig(this.selectedSoftSystem);
    },

    requestValidityCheck: async function () {
      let config = {};
      for (config of this.configurations) {
        if (!(await this.requestSingleValidityCheck(config))) {
          return false;
        }
      }
      return true;
    },

    requestSingleValidityCheck: async function (config) {
      let valid = await requestHandler.validateConfig(this.selectedSoftSystem, config, this.systemProperties);
      console.log(config);
      console.log(`${config.name} is ${valid}`);

      if (!valid) {
        this.openAlternativeModal(config, await this.requestAlternativeConfig(config));
      }

      return valid;
    },

    requestAlternativeConfig: async function (config) {
      let altConfig = await requestHandler.getAlternativeConfig(this.selectedSoftSystem, config, this.systemProperties);
      altConfig.name = 'alternative';
      return altConfig;
    },

    requestConfigEvaluation: async function (index) {
      const evaluatedConfig = await requestHandler.getEvaluatedConfig(
        this.selectedSoftSystem,
        this.configurations[index],
        this.systemProperties
      );
      this.setConfigProperties({ index: index, properties: evaluatedConfig.properties });
      this.setDissectedConfigProperties({ index: index, dissectedProperties: evaluatedConfig.dissectedProperties });
    },

    requestOptimizedConfig: async function (config, propName, maxDifference) {
      const optiConfig = await requestHandler.getOptimizedConfig(
        this.selectedSoftSystem,
        propName,
        maxDifference,
        config,
        this.systemProperties
      );

      return optiConfig;
    },
  },
};

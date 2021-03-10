import { mapFields } from 'vuex-map-fields';

export default {
  data() {
    return {
      // system and config data
      invalidConfig: {},
      alternativeConfig: {},
      optiConfig: {},

      // UI logic data
      displayModal: false,
      displayOptimizationModal: false,
      selectedOptimizationConfigName: '',
      selectedOptimizationPropName: '',
      selectedUnoptimizedConfig: {},
      optimizedConfigFound: false,
      searchedForOptimizedConfig: false,
      selectedInfluenceViewProp: '',
    };
  },

  computed: {
    ...mapFields('configurationStore', ['systemProperties']),

    ...mapFields(`uiLogicStore`, [
      'softSystemLoaded',
      'selectedSoftSystem',
      'previousSelection',
      'chartsDrawn',
      'showInfluences',
      'visibleProperties',
    ]),
  },

  methods: {
    resetUI() {
      this.softSystemLoaded = true;
      this.chartsDrawn = false;
      this.showInfluences = false;

      const visibleProperties = {};
      Object.keys(this.systemProperties).forEach(key => {
        visibleProperties[key] = true;
      });
      this.visibleProperties = visibleProperties;

      this.previousSelection = this.selectedSoftSystem;
    },

    softSystemHasChanged() {
      return this.previousSelection !== this.selectedSoftSystem;
    },

    drawCharts() {
      this.chartsDrawn = true;
    },

    eraseCharts() {
      this.chartsDrawn = false;
    },

    toggleInfluenceArea(selectedProperty) {
      this.selectedInfluenceViewProp = selectedProperty;
      this.showInfluences = !this.showInfluences;
    },

    updateOptimizationStatus(optimizedConfig, unoptimizedConfig) {
      if (optimizedConfig === '') {
        this.optimizedConfigFound = false;
        this.searchedForOptimizedConfig = true;
      } else {
        this.optimizedConfigFound = true;
        this.searchedForOptimizedConfig = true;

        optimizedConfig.name = unoptimizedConfig.name + '[+]';
        this.optiConfig = optimizedConfig;
        this.selectedUnoptimizedConfig = unoptimizedConfig;
      }
    },

    openAlternativeModal(invalidConfig, validAlternativeConfig) {
      this.invalidConfig = invalidConfig;
      this.alternativeConfig = validAlternativeConfig;
      this.$bvModal.show('alternative-config-modal');
    },

    acceptOptimizedConfig() {
      this.optimizedConfigFound = false;
      this.searchedForOptimizedConfig = false;

      this.$nextTick(() => {
        this.$bvModal.hide('optiModal');
      });

      this.addConfig(this.optiConfig, this.optiConfig.name);
    },

    closeModalAcceptAlternative() {
      Object.keys(this.invalidConfig).forEach(key => {
        if (key === 'name') {
          return;
        }
        this.invalidConfig[key] = this.alternativeConfig[key];
      });
      this.displayModal = false;
    },

    openOptimizationModal(configName = '', optimizationPropName = '') {
      this.selectedOptimizationConfigName = configName;
      this.selectedOptimizationPropName = optimizationPropName;
      this.displayOptimizationModal = true;
    },

    closeOptimizationModal() {
      this.optimizedConfigFound = false;
      this.searchedForOptimizedConfig = false;
      this.displayOptimizationModal = false;
    },
  },
};

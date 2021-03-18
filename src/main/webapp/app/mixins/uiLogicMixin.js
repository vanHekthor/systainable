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
      selectedUnoptimizedConfigName: '',
      selectedOptimizationPropName: '',
      globalOptimization: false,
      // selectedUnoptimizedConfig: null,
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

    selectedUnoptimizedConfig() {
      if (this.selectedUnoptimizedConfigName !== '') {
        const selectedConfigIndex = this.findConfigIndex(this.selectedUnoptimizedConfigName);
        this.searchedForOptimizedConfig = false;
        return this.configurations[selectedConfigIndex];
      } else {
        return null;
      }
    },
  },

  methods: {
    resetUI() {
      this.softSystemLoaded = true;
      this.chartsDrawn = false;
      this.showInfluences = false;
      this.previousSelection = this.selectedSoftSystem;
    },

    resetPropertyVisibility() {
      const visibleProperties = {};
      Object.keys(this.systemProperties).forEach(key => {
        visibleProperties[key] = true;
      });
      this.visibleProperties = visibleProperties;
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

    updateOptimizationStatus(optimizedConfig, unoptimizedConfig = null) {
      if (optimizedConfig === '' || optimizedConfig == null) {
        this.optimizedConfigFound = false;
        this.searchedForOptimizedConfig = true;
      } else {
        this.optimizedConfigFound = true;
        this.searchedForOptimizedConfig = true;

        if (!this.globalOptimization) {
          optimizedConfig.name = unoptimizedConfig.name + '[+]';
        } else {
          optimizedConfig.name = this.selectedOptimizationPropName + '[opti]';
        }
        this.optiConfig = optimizedConfig;
        // this.selectedUnoptimizedConfig = unoptimizedConfig;
      }
    },

    openAlternativeModal(invalidConfig, validAlternativeConfig) {
      this.invalidConfig = invalidConfig;
      this.alternativeConfig = validAlternativeConfig;
      this.$bvModal.show('alternative-config-modal');
    },

    acceptOptimizedConfig() {
      this.$nextTick(() => {
        this.$bvModal.hide('optiModal');
      });

      this.addConfig(this.optiConfig, this.optiConfig.name);
      this.closeOptimizationModal();
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

    openOptimizationModal(configName = '', optimizationPropName = '', global = false) {
      this.selectedUnoptimizedConfigName = configName;
      this.selectedOptimizationPropName = optimizationPropName;
      this.globalOptimization = global;
      this.displayOptimizationModal = true;
    },

    closeOptimizationModal() {
      this.optimizedConfigFound = false;
      this.searchedForOptimizedConfig = false;
      this.displayOptimizationModal = false;
      this.selectedUnoptimizedConfigName = '';
      // this.selectedUnoptimizedConfig = null;
    },
  },
};

import { mapMutations } from 'vuex';
import { mapFields } from 'vuex-map-fields';

export default {
  computed: {
    ...mapFields(`configurationStore`, ['configurations']),
  },
  methods: {
    ...mapMutations('configurationStore', [
      'addConfigToStore',
      'deleteConfigFromStore',
      'insertConfigToStore',
      'updateConfigNameFromStore',
      'updateConfigFeature',
    ]),

    updateFeature(index, featureName, value) {
      this.updateConfigFeature({ index, featureName, value });
    },

    updateConfigName(index, configName) {
      this.updateConfigNameFromStore({ index: index, configName: this.findUniqueName(configName, index) });
    },

    findConfigIndex(configName) {
      return this.configurations.findIndex(conf => conf.name === configName);
    },

    addConfig(config, configName = null) {
      if (configName == null) {
        configName = 'config';
        configName = configName + this.configurations.length.toString();
      }

      config.name = this.findUniqueName(configName);

      this.addConfigToStore(config);
    },

    findUniqueName(configName, index = null) {
      const vm = this;
      let lookForNewName = function (name, count) {
        let nameToBeChecked = name;
        if (count !== 0) {
          nameToBeChecked = `${name}(${count})`;
        }
        for (let i = 0; i < vm.configurations.length; i++) {
          if (i == index) {
            continue;
          }
          if (vm.configurations[i].name === nameToBeChecked) {
            count++;
            return lookForNewName(name, count);
          }
        }
        return nameToBeChecked;
      };

      const count = 0;
      return lookForNewName(configName, count);
    },

    deleteConfig(index) {
      this.deleteConfigFromStore(index);
    },

    deleteAllConfigs() {
      this.configurations = [];
    },

    duplicateConfig(index) {
      let configDuplicate = Object.assign({}, this.configurations[index]);
      configDuplicate.name = this.findUniqueName(configDuplicate.name + '(copy)');

      this.insertConfigToStore({ index: index, config: configDuplicate });
    },

    loadConfigs(configs) {
      for (let config of configs) {
        this.addConfig(config, config.name);
      }
    },
  },
};

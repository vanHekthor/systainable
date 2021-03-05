<template id="app">
    <div>
        <div class="top-bar p-d-flex p-ai-center p-shadow-2 mb-3 p-3">
            <b-container class="d-inline-flex flex-wrap align-items-center" fluid>
                <h3 class="mb-0 mr-3 ">Systainable</h3>
                <div class="d-inline-flex align-items-center">
                    <Dropdown class="mr-2" id="selectDropdown" v-model="selectedSoftSystem" :options="softSystems"
                              placeholder="Select a system"
                              @change="requestSystemAttributes"/>
                </div>
            </b-container>
        </div>
        <b-container fluid>
            <ConfigArea
                :systemName="selectedSoftSystem"
                :softSystemLoaded="softSystemLoaded"
                @update-feature="updateFeature"
                @update-config-name="updateConfigName"
                @del-config="deleteConfig"
                @submit-configs="submitConfigs"
                @get-config-example="requestInitConfig"
                @load-data="loadConfigs"
                @duplicate-config="duplicateConfig"
                @click-optimize="openOptimizationModal"
            />

            <b-container class="p-0" fluid>
                <b-button v-if="showInfluences" class="px-2 mb-3 w-100" @click="toggleInfluenceArea()" variant="primary">Back to overview</b-button>
            </b-container>

            <InfluenceArea
                v-if="chartsDrawn && showInfluences"
                :configurations="configurations"
                :selected-property="selectedInfluenceViewProp"
                @click-back="toggleInfluenceArea"
            >
            </InfluenceArea>

            <ChartArea
                v-if="chartsDrawn && configurations.length > 0 && !showInfluences"
                :chartDataArray="chartDataArray"
                :radarData="radarData"
                @click-optimize="openOptimizationModal"
                @click-near-optimum="requestNearOptimalConfig"
                @click-lens="toggleInfluenceArea"
            />
        </b-container>

        <b-modal id="alternative-config-modal" @ok="closeModalAcceptAlternative" centered>
            <template #modal-title>
                {{ invalidConfig.name + ' is invalid'}}
            </template>
            <h6>Suggestion:</h6>
            <div class="p-d-flex p-jc-center p-ai-center" style="overflow-x: auto">
                <ConfigCard class="m-2"
                            :systemFeatures="systemFeatures"
                            :config="invalidConfig"/>
                <font-awesome-icon class="m-2" icon="arrow-right" size="lg" fixed-width/>
                <ConfigCard class="m-2"
                            :systemFeatures="systemFeatures"
                            :config="alternativeConfig"
                            :original-config="invalidConfig"/>
            </div>
        </b-modal>

        <OptimizationModal
            :display-optimization-modal.sync="displayOptimizationModal"
            :selected-optimization-config-name.sync="selectedOptimizationConfigName"
            :selected-optimization-prop-name.sync="selectedOptimizationPropName"
            :config-names="configurations.map(config => { return config.name })"
            :prop-names="Object.keys(systemProperties)"
            :max-optimization-distance="maxOptimizationDistance"
            :searched-for-optimized-config="searchedForOptimizedConfig"
            :optimized-config-found="optimizedConfigFound"
            :system-features="systemFeatures"
            :optimized-config="optiConfig"
            :unoptimized-config="selectedConfig"
            :property-attributes="systemProperties"
            @search-optimized-config="searchOptimizedConfig"
            @ok="acceptOptimizedConfig"
            @hide="closeOptimizationModal">
        </OptimizationModal>
    </div>
</template>

<script>
import chartDataBuilder from "./js_modules/visualization/ChartDataBuilder";
import ConfigArea from "./components/ConfigArea";
import ChartArea from "./components/ChartArea";
import InfluenceArea from "./components/InfluenceArea";
import ConfigCard from "./components/ConfigCard";
import OptimizationModal from "./components/OptimizationModal";

import Dropdown from "primevue/dropdown";
import Button from "primevue/button";
import Dialog from "primevue/dialog";

import requestHandler from "./js_modules/request/requestHandler";

import { mapFields } from "vuex-map-fields";
import { mapMutations } from "vuex";

export default {
    name: 'App',
    components: {
        ConfigArea,
        ChartArea,
        InfluenceArea,
        Dropdown,
        Button,
        Dialog,
        ConfigCard,
        OptimizationModal
    },
    data() {
        return {
            // system and config data
            invalidConfig: {},
            alternativeConfig: {},
            optiConfig: {},

            // UI logic data
            configCount: 0,
            displayModal: false,
            displayOptimizationModal: false,
            selectedOptimizationConfigName: "",
            selectedConfig: {},
            selectedOptimizationPropName: "",
            optimizationDistance: 1,
            optimizedConfigFound: false,
            searchedForOptimizedConfig: false,
            selectedInfluenceViewProp: "",

            // chart data
            chartDataArray: [],
            radarData: {},
        }
    },

    created: async function() {
        this.softSystems = await requestHandler.activateExampleModels();
    },

    computed: {
        ...mapFields(
            `configurationStore`,
            [
                "softSystems",
                "configurations",
                "systemFeatures",
                "systemProperties",
            ]
        ),
        ...mapFields(
            `uiLogicStore`,
            [
                "softSystemLoaded",
                "selectedSoftSystem",
                "previousSelection",
                "chartsDrawn",
                "showInfluences",
                "visibleProperties"
            ]
        ),
        maxOptimizationDistance: function() {
            if (this.softSystemLoaded) {
                return this.systemFeatures.binaryFeatures.length;
            } else {
                return 1;
            }
        },
    },

    methods: {
        ...mapMutations(
            'configurationStore',
            [
                "addConfigToStore",
                "deleteConfigFromStore",
                "insertConfigToStore",
                "updateConfigNameFromStore",
                "updateConfigFeature",
                "setConfigProperties",
                "setDissectedConfigProperties"
            ]
        ),

        updateFeature(index, featureName, value) {
            this.updateConfigFeature({ index, featureName, value });
        },

        updateFeatureNames(features) {
            this.systemFeatures = features;
            this.softSystemLoaded = true;
        },

        updateConfigName(index, configName) {
            this.updateConfigNameFromStore({ index: index, configName: this.findUniqueName(configName, index) });
        },

        addConfig(config, configName = null) {
            if (configName == null) {
                configName = "config";
                this.configCount = this.configurations.length;
                config.name = configName + this.configCount.toString();
            }

            config.name = this.findUniqueName(config.name);

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
            }

            const count = 0;
            return lookForNewName(configName, count);
        },

        deleteConfig(index) {
            this.deleteConfigFromStore(index);

            if (this.configurations.length < 1) {
                this.chartsDrawn = false;
            }
        },

        duplicateConfig(index) {
            let configDuplicate = Object.assign({}, this.configurations[index]);
            configDuplicate.name = this.findUniqueName(configDuplicate.name + "(copy)");

            this.insertConfigToStore({index: index, config: configDuplicate});
        },

        loadConfigs(configs) {
            // this.configurations = this.configurations.concat(configs);

            for (let config of configs) {
                this.addConfig(config, config.name);
            }
        },

        drawCharts() {
            this.chartsDrawn = true;
        },

        toggleInfluenceArea(selectedProperty) {
            this.selectedInfluenceViewProp = selectedProperty;
            this.showInfluences = !this.showInfluences;
        },

        searchOptimizedConfig: async function(optiModalEvent, configName, propName, maxDifference) {
            optiModalEvent.preventDefault();

            const selectedConfigIndex = this.configurations.findIndex(conf => conf.name === configName);
            this.selectedConfig = this.configurations[selectedConfigIndex]

            if (await this.requestSingleValidityCheck(this.selectedConfig)) {
                await this.requestConfigEvaluation(selectedConfigIndex);
                let optiConfig = await this.requestOptimizedConfig(this.selectedConfig, propName, maxDifference);

                if (optiConfig === "") {
                    this.optimizedConfigFound = false;
                    this.searchedForOptimizedConfig = true;
                } else {
                    this.optimizedConfigFound = true;
                    this.searchedForOptimizedConfig = true;
                    optiConfig.name = this.selectedConfig.name + "[+]";
                    this.optiConfig = optiConfig;
                }
            }
        },

        acceptOptimizedConfig() {
            this.optimizedConfigFound = false;
            this.searchedForOptimizedConfig = false;

            this.$nextTick(() => {
                this.$bvModal.hide('optiModal');
            });

            this.addConfig(this.optiConfig, this.optiConfig.name);
        },

        // requests to backend
        requestSystemAttributes: async function(event) {
            if (event.value !== this.previousSelection) {
                let featureNames = await requestHandler.getFeatureNames(event.value);
                this.updateFeatureNames(featureNames);
                this.systemProperties = await requestHandler.getPropNames(event.value);

                this.chartsDrawn = false;
                this.showInfluences = false;

                const visibleProperties = {};
                Object.keys(this.systemProperties).forEach(key => {
                    visibleProperties[key] = true;
                });
                this.visibleProperties = visibleProperties;

                this.configurations = [];
                this.configCount = 0;
                await this.requestInitConfig();
                this.previousSelection = event.value;
            }
        },

        requestInitConfig: async function() {
            let config = await requestHandler.getInitConfig(this.selectedSoftSystem);
            this.addConfig(config);
        },

        requestValidityCheck: async function() {
            let config = {};
            for (config of this.configurations) {
                if (!await this.requestSingleValidityCheck(config)) {
                    return false;
                }
            }
            return true;
        },

        requestSingleValidityCheck: async function(config) {
            let valid = await requestHandler.validateConfig(this.selectedSoftSystem, config, this.systemProperties);
            console.log(config);
            console.log(`${config.name} is ${valid}`);

            if (!valid) {
                this.invalidConfig = config;
                this.alternativeConfig = await this.requestAlternativeConfig(config);
                this.$bvModal.show('alternative-config-modal');

                return false;
            }

            return true;
        },

        requestAlternativeConfig: async function(config) {
            let altConfig = await requestHandler.getAlternativeConfig(this.selectedSoftSystem, config, this.systemProperties);
            altConfig.name = "alternative"
            return altConfig;
        },

        submitConfigs: async function() {
            if (await this.requestValidityCheck()) {
                let configNames = [];
                let propertyValueMaps = [];

                for (const [index, config] of this.configurations.entries()) {
                    configNames.push(config.name);

                    await this.requestConfigEvaluation(index);

                    let propMap = new Map();
                    Object.keys(this.systemProperties).forEach(function(key) {
                        propMap.set(key, config.properties[key]);
                    })

                    propertyValueMaps.push(propMap);
                }

                this.chartDataArray = chartDataBuilder.buildBarChartData(configNames, this.systemProperties, propertyValueMaps);
                this.radarData = chartDataBuilder.buildRadarData(configNames, this.systemProperties, propertyValueMaps);
                this.drawCharts();
            }
        },

        requestConfigEvaluation: async function(index) {
            const evaluatedConfig = await requestHandler.getEvaluatedConfig(this.selectedSoftSystem, this.configurations[index], this.systemProperties);
            this.setConfigProperties({ index: index, properties: evaluatedConfig.properties });
            this.setDissectedConfigProperties({ index: index, dissectedProperties: evaluatedConfig.dissectedProperties });
        },

        requestOptimizedConfig: async function(config, propName, maxDifference) {
            let optiConfig = await requestHandler.getOptimizedConfig(this.selectedSoftSystem, propName, maxDifference, config, this.systemProperties);

            return optiConfig;
        },

        requestNearOptimalConfig: async function(propName) {
            let config =  await requestHandler.getNearOptimalConfig(this.selectedSoftSystem, propName);

            this.addConfig(config);
            config.name = propName + '[opti]';

            return config;
        },

        // methods for handling a modals
        closeModalAcceptAlternative() {
            Object.keys(this.invalidConfig).forEach(key => {
                if (key === 'name') {
                    return;
                }
                this.invalidConfig[key] = this.alternativeConfig[key];
            })
            this.displayModal = false;
        },

        openOptimizationModal(configName = "", optimizationPropName = "") {
            if (this.configurations.length === 1) {
                this.selectedOptimizationConfigName = this.configurations[0].name;
            } else {
                this.selectedOptimizationConfigName = configName;
            }
            this.selectedOptimizationPropName = optimizationPropName;
            this.displayOptimizationModal = true;
        },

        closeOptimizationModal() {
            this.optimizedConfigFound = false;
            this.searchedForOptimizedConfig = false;
            this.displayOptimizationModal = false;
        }
    }

}
</script>

<style>
#app{
}

html {
    font-size: 14px;
}

html, body {
    padding: 0 !important;
    margin: 0 !important;
}

.top-bar {
    background-color: white;
}
</style>


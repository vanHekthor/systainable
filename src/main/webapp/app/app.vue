<template id="app">
    <div>
        <div class="top-bar p-d-flex p-ai-center p-shadow-2 mb-3 p-3">
            <b-container class="d-inline-flex flex-wrap align-items-center" fluid>
                <h3 class="mb-0 mr-3 ">Green Configurator</h3>
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
                :configurationFeatures="configurationFeatures"
                :configurations="configurations"
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

            <ChartArea
                v-if="draw & configurations.length > 0"
                :configurationProperties="configurationProperties"
                :chartDataArray="chartDataArray"
                :radarData="radarData"
                @click-optimize="openOptimizationModal"
            />
        </b-container>

        <b-modal id="alternative-config-modal" @ok="closeModalAcceptAlternative" centered>
            <template #modal-title>
                {{ invalidConfig.name + ' is invalid'}}
            </template>
            <h6>Suggestion:</h6>
            <div class="p-d-flex p-jc-center p-ai-center" style="overflow-x: auto">
                <ConfigCard class="m-2"
                            :configurationFeatures="configurationFeatures"
                            :config="invalidConfig"/>
                <font-awesome-icon class="m-2" icon="arrow-right" size="lg" fixed-width/>
                <ConfigCard class="m-2"
                            :configurationFeatures="configurationFeatures"
                            :config="alternativeConfig"
                            :original-config="invalidConfig"/>
            </div>
        </b-modal>

        <OptimizationModal
            :display-optimization-modal.sync="displayOptimizationModal"
            :selected-optimization-config-name.sync="selectedOptimizationConfigName"
            :selected-optimization-prop-name.sync="selectedOptimizationPropName"
            :config-names="configurations.map(config => { return config.name })"
            :prop-names="Object.keys(configurationProperties)"
            :max-optimization-distance="maxOptimizationDistance"
            :searched-for-optimized-config="searchedForOptimizedConfig"
            :optimized-config-found="optimizedConfigFound"
            :configuration-features="configurationFeatures"
            :optimized-config="optiConfig"
            :unoptimized-config="selectedConfig"
            :property-attributes="configurationProperties"
            @search-optimized-config="searchOptimizedConfig"
            @ok="acceptOptimizedConfig"
            @hide="closeOptimizationModal">
        </OptimizationModal>
    </div>
</template>

<script>
import chartDataBuilder from './js_modules/visualization/ChartDataBuilder';
import ConfigArea from './components/ConfigArea';
import ChartArea from './components/ChartArea';
import ConfigCard from "./components/ConfigCard";
import OptimizationModal from "./components/OptimizationModal";

import Dropdown from 'primevue/dropdown';
import Button from 'primevue/button';
import Dialog from 'primevue/dialog';

import requestHandler from './js_modules/request/requestHandler';

export default {
    name: 'App',
    components: {
        ConfigArea,
        ChartArea,
        Dropdown,
        Button,
        Dialog,
        ConfigCard,
        OptimizationModal
    },
    data() {
        return {
            // system and config data
            softSystems: [],
            configurationFeatures: [],
            configurationProperties: {},
            configurations: [],
            invalidConfig: {},
            alternativeConfig: {},
            optiConfig: {},

            // UI logic data
            selectedSoftSystem: "",
            previousSelection: "",
            configCount: 0,
            displayModal: false,
            displayOptimizationModal: false,
            selectedOptimizationConfigName: "",
            selectedConfig: {},
            selectedOptimizationPropName: "",
            optimizationDistance: 1,
            optimizedConfigFound: false,
            searchedForOptimizedConfig: false,

            draw: false,
            softSystemLoaded: false,

            // chart data
            chartDataArray: [],
            radarData: {},
        }
    },
    mounted() {
        if(sessionStorage.selectedSoftSystem) {
            this.selectedSoftSystem = JSON.parse(sessionStorage.selectedSoftSystem);
        }
        if(sessionStorage.configurationFeatures) {
            this.configurationFeatures = JSON.parse(sessionStorage.configurationFeatures);
        }
        if(sessionStorage.configurationProperties) {
            this.configurationProperties = JSON.parse(sessionStorage.configurationProperties);
        }
        if(sessionStorage.configurations) {
            this.configurations = JSON.parse(sessionStorage.configurations);
        }
        if (sessionStorage.softSystemLoaded) {
            this.softSystemLoaded = JSON.parse(sessionStorage.softSystemLoaded);
        }
        if (sessionStorage.configCount) {
            this.configCount = JSON.parse(sessionStorage.configCount);
        }
    },
    watch: {
        selectedSoftSystem: {
            handler(newSystem) {
                sessionStorage.selectedSoftSystem = JSON.stringify(newSystem);
            },
            deep: true
        },
        configurationFeatures: {
            handler(newConfigFeat) {
                sessionStorage.configurationFeatures = JSON.stringify(newConfigFeat);
            },
            deep: true
        },
        configurationProperties: {
            handler(newConfigProp) {
                sessionStorage.configurationProperties = JSON.stringify(newConfigProp);
            },
            deep: true
        },
        configurations: {
            handler(newConfig) {
                sessionStorage.configurations = JSON.stringify(newConfig);
            },
            deep: true
        },
        softSystemLoaded: {
            handler(newSoftSystemLoaded) {
                sessionStorage.softSystemLoaded = JSON.stringify(newSoftSystemLoaded);
            },
            deep: true
        },
        configCount: {
            handler(newConfigCount) {
                sessionStorage.configCount = JSON.stringify(newConfigCount);
            },
            deep: true
        }
    },

    created: async function() {
        this.softSystems = await requestHandler.activateExampleModels();
    },

    computed: {
        maxOptimizationDistance: function() {
            if (this.softSystemLoaded) {
                return this.configurationFeatures.binaryFeatures.length;
            } else {
                return 1;
            }
        },
    },

    methods: {
        updateFeature(index, featureName, value) {
            this.configurations[index][featureName] = value;
        },

        updateFeatureNames(features) {
            this.configurationFeatures = features;
            this.softSystemLoaded = true;
        },

        updateConfigName(index, configName) {
            let count = 0;

            this.configurations.forEach(function(config, idx) {
                if (index !== idx  && config.name === configName) {
                    count++;
                }
            })
            if (count > 0) {
                configName += `(${count})`
            }
            this.configurations[index].name = configName;
        },

        addConfig(config) {
            this.configCount = this.configurations.length;
            config.name += this.configCount.toString();
            this.configurations.push(config);
            this.updateConfigName(this.configurations.length - 1, config.name)
        },

        deleteConfig(index) {
            this.configurations.splice(index, 1);
            if (this.configurations.length < 1) {
                this.draw = false;
            }
        },

        duplicateConfig(index) {
            let configDuplicate = Object.assign({}, this.configurations[index]);
            configDuplicate.name = configDuplicate.name + "(copy)"

            const insert = (arr, index, newItem) => [
                ...arr.slice(0, index),
                newItem,
                ...arr.slice(index)
            ]

            this.configurations = insert(this.configurations, index + 1, configDuplicate);
        },

        loadConfigs(configs) {
            this.configurations = this.configurations.concat(configs);
        },

        drawCharts() {
            this.draw = true;
        },

        searchOptimizedConfig: async function(optiModalEvent, configName, propName, maxDifference) {
            optiModalEvent.preventDefault();

            this.selectedConfig = this.configurations.find(conf => { return conf.name === configName });

            if (await this.requestSingleValidityCheck(this.selectedConfig)) {
                await this.requestConfigEvaluation(this.selectedConfig);
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
            this.configurations.push(this.optiConfig);
            this.optimizedConfigFound = false;
            this.searchedForOptimizedConfig = false;

            this.$nextTick(() => {
                this.$bvModal.hide('optiModal');
            });
        },

        // requests to backend
        requestSystemAttributes: async function(event) {
            let featureNames = await requestHandler.getFeatureNames(event.value);
            this.updateFeatureNames(featureNames);
            this.configurationProperties = await requestHandler.getPropNames(event.value);

            if (event.value !== this.previousSelection) {
                this.configurations = [];
                this.configCount = 0;
                await this.requestInitConfig();
                this.previousSelection = event.value;
                this.draw = false;
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
            let valid = await requestHandler.validateConfig(this.selectedSoftSystem, config, this.configurationProperties);
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
            let altConfig = await requestHandler.getAlternativeConfig(this.selectedSoftSystem, config, this.configurationProperties);
            altConfig.name = "alternative"
            return altConfig;
        },

        submitConfigs: async function() {
            if (await this.requestValidityCheck()) {
                let configNames = [];
                let config = {};
                let propertyValueMaps = [];

                for (config of this.configurations) {
                    configNames.push(config.name);

                    await this.requestConfigEvaluation(config);

                    let propMap = new Map();
                    Object.keys(this.configurationProperties).forEach(function(key) {
                        propMap.set(key, config.properties[key]);
                    })

                    propertyValueMaps.push(propMap);
                }

                this.chartDataArray = chartDataBuilder.buildBarChartData(configNames, this.configurationProperties, propertyValueMaps);
                this.radarData = chartDataBuilder.buildRadarData(configNames, this.configurationProperties, propertyValueMaps);
                this.drawCharts();
            }
        },

        requestConfigEvaluation: async function(config) {
            config.properties =  await requestHandler.getConfigPropValues(this.selectedSoftSystem, config, this.configurationProperties);
        },

        requestOptimizedConfig: async function(config, propName, maxDifference) {
            let optiConfig = await requestHandler.getOptimizedConfig(this.selectedSoftSystem, propName, maxDifference, config, this.configurationProperties);

            return optiConfig;
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

        openOptimizationModal(optimizationPropName) {
            if (this.configurations.length === 1) {
                this.selectedOptimizationConfigName = this.configurations[0].name;
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

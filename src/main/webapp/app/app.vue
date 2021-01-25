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
                @submit-configs="requestValidityCheck"
                @get-config-example="requestInitConfig"
                @load-data="loadConfigs"
                @duplicate-config="duplicateConfig"
            />

            <ChartArea
                v-if="draw & configurations.length > 0"
                :configurationProperties="configurationProperties"
                :chartDataArray="chartDataArray"
                :radarData="radarData"
            />
        </b-container>
        <Dialog :header="invalidConfig.name + ' is invalid'" :visible.sync="displayModal" :style="{width: '50vw'}" :modal="true">
            <h6>Suggestion:</h6>
            <div class="p-d-flex p-jc-center p-ai-center" style="overflow-x: auto">
                <ConfigCard class="m-2"
                            :configurationFeatures="configurationFeatures"
                            :config="invalidConfig"/>
                <font-awesome-icon class="m-2" icon="arrow-right" size="lg" fixed-width/>
                <ConfigCard class="m-2"
                            :configurationFeatures="configurationFeatures"
                            :config="alternativeConfig"/>
            </div>
            <template #footer>
                <Button label="Decline" icon="pi pi-times" @click="closeModal" class="p-button-text"/>
                <Button label="Accept" icon="pi pi-check" @click="closeModalAcceptAlternative" autofocus />
            </template>
        </Dialog>
    </div>
</template>

<script>
import chartDataBuilder from './js_modules/visualization/ChartDataBuilder'
import ConfigArea from './components/ConfigArea';
import ChartArea from './components/ChartArea';
import ConfigCard from "./components/ConfigCard";


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
        ConfigCard
    },
    data() {
        return {
            // system and config data
            softSystems: [],
            configurationFeatures: [],
            configurationProperties: [],
            configurations: [],
            invalidConfig: {},
            alternativeConfig: {},

            // UI logic data
            selectedSoftSystem: "",
            previousSelection: "",
            configCount: 0,
            displayModal: false,
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

    created: async function requestAvailableSystems() {
        this.softSystems = await requestHandler.getAvailableSystems();
    },

    computed: {
        requestAlternativeConfig: async function() {
            let altConfig = await requestHandler.getInitConfig(this.selectedSoftSystem);
            altConfig.name = "alternative"
            return altConfig;
        }
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

            // function rename(index, name, count) {
            //     console.log('hallo');
            //     console.log(index);
            //     console.log(name);
            //     console.log(count);
            //
            //     this.configurations.forEach(function(config, idx) {
            //         if (index !== idx  && config.name === name) {
            //             count++;
            //             rename(index, configName + `(${count})`, count);
            //         }
            //     })
            //     return configName + `(${count})`;
            // }
            //
            // console.log(rename(index, configName, count));

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
                let valid = await requestHandler.validateConfig(this.selectedSoftSystem, config, this.configurationProperties);
                console.log(config);
                console.log(`${config.name} is ${valid}`);
                if (!valid) {
                    this.invalidConfig = config;
                    this.alternativeConfig = await this.requestAlternativeConfig;
                    this.openModal();

                    return;
                }
            }
            await this.submitConfigs();
        },

        submitConfigs: async function() {
            let configNames = [];
            let config = {};
            let propertyValueMaps = [];

            for (config of this.configurations) {
                configNames.push(config.name);

                let predictedProps =
                    await requestHandler.getConfigPropValues(this.selectedSoftSystem, config, this.configurationProperties)

                let propMap = new Map();
                Object.keys(this.configurationProperties).forEach(function(key) {
                    propMap.set(key, predictedProps[key]);
                })

                propertyValueMaps.push(propMap);
            }

            this.chartDataArray = chartDataBuilder.buildBarChartData(configNames, this.configurationProperties, propertyValueMaps);
            this.radarData = chartDataBuilder.buildRadarData(configNames, this.configurationProperties, propertyValueMaps);
            this.drawCharts();
        },

        // methods for handling a modal indicating that an invalid configuration has been made
        openModal() {
            this.displayModal = true;
        },

        closeModal() {
            this.displayModal = false;
        },

        closeModalAcceptAlternative() {
            Object.keys(this.invalidConfig).forEach(key => {
                if (key === 'name') {
                    return;
                }
                this.invalidConfig[key] = this.alternativeConfig[key];
            })
            this.displayModal = false;
        },
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

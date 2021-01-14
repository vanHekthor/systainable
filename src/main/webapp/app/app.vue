<template id="app">
    <div>
        <div class="top-bar p-d-flex p-ai-center p-shadow-2 p-mb-3 p-p-3">
            <h3 class="p-mb-0 p-mr-2">Green Configurator</h3>
            <div>
                <Dropdown id="selectDropdown" v-model="selectedSoftSystem" :options="softSystems" optionLabel="name"
                          placeholder="Select a system"
                          @change="getSystemFeatures"/>
            </div>

            <Dialog :header="invalidConfig + ' is invalid'" :visible.sync="displayModal" :style="{width: '50vw'}" :modal="true">
                <h6>Suggestion:</h6>
                <div class="p-d-flex p-ai-center">
                    <label class="p-mr-2" for="encryption">encryption</label>
                    <input class="p-mr-2" id="encryption" type="checkbox" :checked="true"/>
                    <label class="p-mr-2" for="crypt_aes">crypt_aes</label>
                    <input class="p-mr-2" id="crypt_aes" type="checkbox" :checked="true"/>
                    <label class="p-mr-2" for="crypt_blowfish">crypt_blowfish</label>
                    <input class="p-mr-2" id="crypt_blowfish" type="checkbox" :checked="false"/>
                </div>
                <template #footer>
                    <Button label="Decline" icon="pi pi-times" @click="closeModal" class="p-button-text"/>
                    <Button label="Accept" icon="pi pi-check" @click="closeModalAccept" autofocus />
                </template>
            </Dialog>
        </div>

        <ConfigArea
            :systemName="selectedSoftSystem.name"
            :configurationFeatures="configurationFeatures"
            :configurations="configurations"
            :softSystemLoaded="softSystemLoaded"
            @update-feature="updateFeature"
            @update-config-name="updateConfigName"
            @del-config="deleteConfig"
            @submit-configs="checkValidity"
            @get-config-example="getConfigExample"
            @load-data="loadConfigs"
        />

        <ChartArea
            v-if="draw & configurations.length > 0"
            :chartDataArray="chartDataArray"
            :radarData="radarData"
        />
    </div>
</template>

<script>
import api from './Api';
import chartDataBuilder from './ChartDataBuilder'
import ConfigArea from './components/ConfigArea';
import ChartArea from './components/ChartArea';
import Dropdown from 'primevue/dropdown';
import Button from 'primevue/button';
import Dialog from 'primevue/dialog';
import requestHandler from './requestHandler';

export default {
    name: 'App',
    components: {
        ConfigArea,
        ChartArea,
        Dropdown,
        Button,
        Dialog
    },
    data() {
        return {
            messages: [],
            displayModal: false,
            invalidConfig: "",
            featureModel: "",
            configurationFeatures: [],
            configurationProperties: [],
            configurations: [],
            configCount: 0,
            chartDataArray: [],
            radarData: {},
            draw: false,
            selectedSoftSystem: {},
            softSystemLoaded: false,
            softSystems: [
                {name: 'BlueSoft'},
                {name: 'RedSoft'},
                {name: 'GreenSoft'},
            ]
        }
    },
    mounted() {
        if(sessionStorage.selectedSoftSystem) {
            this.selectedSoftSystem = JSON.parse(sessionStorage.selectedSoftSystem);
        }
        if(sessionStorage.configurationFeatures) {
            this.configurationFeatures = JSON.parse(sessionStorage.configurationFeatures);
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


    methods: {
        //turns feature in the cell(configIndex, featureIndex) on/off
        updateFeature(index, featureName) {
            this.configurations[index][featureName]
                = !this.configurations[index][featureName];
        },

        updateConfigName(index, configName) {
            this.configurations[index].name = configName;
        },

        //requests features from backend
        getSystemFeatures() {
            // using example data because JSON-Parser is still WIP
            // request itself works
            try {
                let featureNames = api.getFeatureNamesExample();
                //let featureNames = requestHandler.getFeatureNames();
                this.updateFeatureNames(featureNames);
            } catch (error) {
                this.$log.debug(error)
                this.error = "Failed to load features";
            }finally {
                this.loading = false;
            }
            if (this.configurations.length < 1) {
                this.getConfigExample();
            }

        },

        updateFeatureNames(featureNames) {
            // let names = [];
            // let featureName;
            // for (featureName of featureNames) {
            //     names.push({name: featureName});
            // }
            this.configurationFeatures = featureNames;
            this.softSystemLoaded = true;
        },

        getConfigExample() {
            try {
                let featureSet = api.getConfigExample();
                //let featureSet = requestHandler.getConfig();
                this.addConfig(featureSet);
            } catch (error) {
                this.$log.debug(error)
                this.error = "Failed to load config example";
            }finally {
                this.loading = false;
            }
        },

        addConfig(config) {
            config.name += this.configCount.toString();
            this.configurations.push(config);
            this.configCount++;
        },

        deleteConfig(index) {
            console.log("remove " + index);
            this.configurations.splice(index, 1);
            if (this.configurations.length < 1) {
                this.draw = false;
            }
        },

        loadConfigs(configs) {
            this.configurations = this.configurations.concat(configs);
        },

        submitConfigs() {
            let configNames = [];
            let config = {};
            let propertyValueMaps = [];
            for (config of this.configurations) {
                configNames.push(config.name);
                propertyValueMaps.push(api.getPropertiesExample());
                //propertyValueMaps.push(requestHandler.getFeatureProperties());
            }

            this.chartDataArray = chartDataBuilder.buildBarChartData(configNames, propertyValueMaps);

            this.radarData = chartDataBuilder.buildRadarData(configNames, propertyValueMaps);
            this.drawCharts();
        },

        checkValidity() {
            let config = {};
            for (config of this.configurations) {
                if (!api.getValidity(config)) {
                    this.invalidConfig = config.name;
                    this.openModal();

                    return;
                }
            }
            this.submitConfigs();
        },

        drawCharts() {
            this.draw = true;
        },
        openModal() {
            this.displayModal = true;
        },
        closeModal() {
            this.displayModal = false;
        },
        closeModalAccept() {
            let config = {};
            for (config of this.configurations) {
                if (config.name === this.invalidConfig) {
                    config.encryption = true;
                    config.crypt_aes = true;
                    config.crypt_blowfish = false;
                }
            }
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

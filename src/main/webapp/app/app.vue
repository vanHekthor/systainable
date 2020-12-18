<template id="app">
    <div>
        <div class="top-bar p-d-flex p-ai-center p-shadow-2 p-mb-3 p-p-3">
            <h3 class="p-mb-0 p-mr-2">Green Configurator</h3>
            <div>
                <Dropdown id="selectDropdown" v-model="selectedSoftSystem" :options="softSystems" optionLabel="name"
                          placeholder="Select a system"
                          @change="getFeatures"/>
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

        <ConfigTable
            :configurationFeatures="configurationFeatures"
            :configurations="configurations"
            :softSystemLoaded="softSystemLoaded"
            v-on:update-feature="updateFeature"
            v-on:update-config-name="updateConfigName"
            v-on:del-config="deleteConfig"
            v-on:submit-configs="checkValidity"
            v-on:get-config-example="getConfigExample"/>

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
import ConfigTable from './components/ConfigTable';
import ChartArea from './components/ChartArea';
import Dropdown from 'primevue/dropdown';
import Button from 'primevue/button';
import Dialog from 'primevue/dialog';




export default {
    name: 'App',
    components: {
        ConfigTable,
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
            selectedSoftSystem: null,
            softSystemLoaded: false,
            softSystems: [
                {name: 'BlueSoft'},
                {name: 'RedSoft'},
                {name: 'GreenSoft'},
            ]
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
        getFeatures() {
            // using example data because JSON-Parser is still WIP
            // request itself works
            try {
                let featureNames = api.getFeatureNamesExample();
                console.log(featureNames);
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
            let names = [];
            let featureName;
            for (featureName of featureNames) {
                names.push({name: featureName});
            }
            this.configurationFeatures = names;
            this.softSystemLoaded = true;
        },

        getConfigExample() {
            try {
                let featureSet = api.getConfigExample();
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

        submitConfigs() {
            let configNames = [];
            let config = {};
            let propertyValueMaps = [];
            for (config of this.configurations) {
                configNames.push(config.name);
                propertyValueMaps.push(api.getProperties());
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
.logo {
    height: 1.5rem;
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

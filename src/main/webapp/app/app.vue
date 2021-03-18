<template id="app">
    <div>
        <div class="top-bar p-d-flex p-ai-center p-shadow-2 mb-3 py-2 px-3">
            <b-container class="p-0 d-inline-flex flex-wrap align-items-center" fluid>
                <img class="mr-3" src="content/images/systainable.png" height="50"/>
                <div class="d-inline-flex align-items-center">
                    <Dropdown class="mr-2" id="selectDropdown" v-model="selectedSoftSystem" :options="softSystems"
                              placeholder="Select a system"
                              @change="loadSoftSystem"/>
                </div>
            </b-container>
        </div>
        <b-container fluid>
            <ConfigArea
                :systemName="selectedSoftSystem"
                :softSystemLoaded="softSystemLoaded"
                @update-feature="updateFeature"
                @update-config-name="updateConfigName"
                @del-config="startConfigDeletion"
                @submit-configs="submitConfigs"
                @get-config-example="addInitialConfig"
                @load-data="loadConfigs"
                @duplicate-config="duplicateConfig"
                @click-optimize="startConfigOptimization"
                @click-near-optimum="startConfigOptimization"
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
                @click-optimize="startConfigOptimization"
                @click-near-optimum="startConfigOptimization"
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
            :global-optimization = "globalOptimization"
            :display-optimization-modal.sync="displayOptimizationModal"
            :selected-unoptimized-config-name.sync="selectedUnoptimizedConfigName"
            :selected-optimization-prop-name.sync="selectedOptimizationPropName"
            :config-names="configurations.map(config => { return config.name })"
            :prop-names="Object.keys(systemProperties)"
            :max-optimization-distance="maxOptimizationDistance"
            :searched-for-optimized-config="searchedForOptimizedConfig"
            :optimized-config-found="optimizedConfigFound"
            :system-features="systemFeatures"
            :optimized-config="optiConfig"
            :unoptimized-config="selectedUnoptimizedConfig"
            :property-attributes="systemProperties"
            @search-optimized-config="searchOptimizedConfig"
            @change-global-prop="searchNearOptimalConfig"
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

import requestMixin from "./mixins/requestMixin";
import configManagementMixin from "./mixins/configManagementMixin";
import uiLogicMixin from "./mixins/uiLogicMixin";

import { maxLinearSteps, maxExponentialSteps } from "./js_modules/util/maxStepsUtil";

export default {
    name: 'App',
    title: 'Systainable',
    mixins: [requestMixin, configManagementMixin, uiLogicMixin],
    components: {
        ConfigArea,
        ChartArea,
        InfluenceArea,
        Dropdown,
        ConfigCard,
        OptimizationModal
    },
    data() {
        return {
            // chart data
            chartDataArray: [],
            radarData: {},
        }
    },

    computed: {
        maxOptimizationDistance: function() {
            if (this.softSystemLoaded && this.systemFeatures != null && this.selectedUnoptimizedConfig != null) {
                let maxSteps = 0;

                for (const [key, numericFeatureObject] of Object.entries(this.systemFeatures.numericFeatures)) {

                    let numericValue = this.selectedUnoptimizedConfig[key];

                    if (numericFeatureObject.stepFunction.split(' ')[1] === '+') {
                        maxSteps = maxLinearSteps(
                            numericValue, numericFeatureObject.min, numericFeatureObject.max, parseInt(numericFeatureObject.stepFunction.split(' ')[2])
                        );
                        console.log(key + `-maxSteps: ${maxSteps}`);

                    } else if (numericFeatureObject.stepFunction.split(' ')[1] === '*') {
                        maxSteps = maxExponentialSteps(
                            numericValue, numericFeatureObject.min, numericFeatureObject.max, parseInt(numericFeatureObject.stepFunction.split(' ')[2])
                        );
                        console.log(key + `-maxSteps: ${maxSteps}`);
                    }
                }
                return Math.max(this.systemFeatures.binaryFeatures.length, maxSteps);

            } else {
                return 1;
            }
        },
    },

    methods: {
        loadSoftSystem: async function(event) {
            if (this.softSystemHasChanged()) {
                this.resetUI();
                await this.requestSystemAttributes(event);
                this.resetPropertyVisibility();
                this.deleteAllConfigs();
                await this.addInitialConfig();
            }
        },

        addInitialConfig: async function() {
            this.addConfig(await this.requestInitConfig());
        },

        startConfigDeletion(index) {
            this.deleteConfig(index);

            if (this.configurations.length < 1) {
                this.eraseCharts();
            }
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

        startConfigOptimization: async function(configName = "", optimizationProbName = "", global = false) {
            if (this.configurations.length === 1) {
                configName = this.configurations[0].name;
            }

            this.openOptimizationModal(configName, optimizationProbName, global);

            if (global && optimizationProbName !== "") {
                await this.searchNearOptimalConfig(optimizationProbName);
            }
        },

        searchOptimizedConfig: async function(optiModalEvent, configName, propName, maxDifference) {
            optiModalEvent.preventDefault();

            const selectedConfigIndex = this.findConfigIndex(configName);
            const unoptimizedConfig = this.configurations[selectedConfigIndex];

            if (await this.requestSingleValidityCheck(unoptimizedConfig)) {
                await this.requestConfigEvaluation(selectedConfigIndex);
                let optimizedConfig = await this.requestOptimizedConfig(unoptimizedConfig, propName, maxDifference);

                this.updateOptimizationStatus(optimizedConfig, unoptimizedConfig);
            }
        },

        searchNearOptimalConfig: async function(propName) {
            this.updateOptimizationStatus(await this.requestNearOptimalConfig(propName));
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


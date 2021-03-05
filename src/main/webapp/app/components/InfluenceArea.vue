<template>
    <b-container fluid>
        <b-row>
            <b-col class="px-2 mb-3" cols="12" lg="6" v-for="(n, index) in 2" :key="index">
                <template>
                    <b-card>
                        <b-row class="px-2">
                            <b-col class="p-0">
                                <b-form-select :value="computeSelectedConfigNames" v-model="selectedConfigNames[index]" @input="onSelectedConfigNameChanged(index)"
                                               :options="configurations.map(config => {return config.name})">
                                    <template #first>
                                        <b-form-select-option value="" disabled>-- Please select a configuration --</b-form-select-option>
                                    </template>
                                </b-form-select>
                            </b-col>
                            <b-col class="px-1">
                                <b-form-select v-model="selectedPropertyNames[index]" :options="Object.keys(configurations[0].properties)">
                                    <template #first>
                                        <b-form-select-option value="" disabled>-- Please select a property --</b-form-select-option>
                                    </template>
                                </b-form-select>
                            </b-col>
                        </b-row>
                        <template v-if="selectedConfigNames[index] !== '' && checkVisualizable(selectedConfigs[index])">
                            <InfluenceChart :chart-data="buildStackedInfluencesBarChartData(selectedConfigs[index], selectedPropertyNames[index])"
                                            :options="stackedOptions(selectedPropertyNames[index])">
                            </InfluenceChart>
                            <InfluenceChart :chart-data="buildOrderedInfluencesBarChartData(selectedConfigs[index], selectedPropertyNames[index])"
                                            :options="orderedInfluencesOptions(selectedPropertyNames[index])">
                            </InfluenceChart>
                        </template>
                    </b-card>
                </template>
            </b-col>
        </b-row>
    </b-container>
</template>

<script>
import InfluenceChart from "./InfluenceChart";
import ChartDataBuilder from "../js_modules/visualization/ChartDataBuilder";
import { mapFields } from "vuex-map-fields";

export default {
    name: "InfluenceArea",

    components: {
        InfluenceChart
    },

    props: {
        configurations: {
            type: Array,
            required: true
        },
        selectedProperty: {
            type: String,
            required: true
        }
    },

    data() {
        return {
            selectedConfigNames: [this.configurations[0].name, this.configurations[Math.min(this.configurations.length - 1, 1)].name],
            selectedConfigs: [{}, {}],
            selectedPropertyNames: [this.selectedProperty, this.selectedProperty],
        }
    },

    created: function(){
        this.selectedConfigs[0] = this.getSelectedConfig(this.selectedConfigNames[0]);
        this.selectedConfigs[1] = this.getSelectedConfig(this.selectedConfigNames[1]);
    },

    computed: {
        ...mapFields(
            `configurationStore`,
            [
                "systemProperties",
            ]
        ),
        computeSelectedConfigNames() {
            let checkSelectedConfigA = false;
            let checkSelectedConfigB = false;

            // Do the previously selected configuration (names) still exist?
            for (let config of this.configurations) {
                if (config.name === this.selectedConfigNames[0]) {
                    checkSelectedConfigA = true;
                }
                if (config.name === this.selectedConfigNames[1]) {
                    checkSelectedConfigB = true;
                }
            }

            // If a previously selected configuration (name) does not exist anymore it is set to "". Otherwise the selection is kept.
            let selectionA = "";
            let selectionB = "";

            if (!checkSelectedConfigA) {
                selectionA = "";
            } else {
                selectionA = this.selectedConfigNames[0];
            }
            if (!checkSelectedConfigB) {
                selectionB = "";
            } else {
                selectionB = this.selectedConfigNames[1];
            }

            // update the selection
            this.selectedConfigNames = [selectionA, selectionB];

            return [selectionA, selectionB];
        }
    },

    methods: {
        buildOrderedInfluencesBarChartData(config, propName) {
            if (config.dissectedProperties != null) {
                return ChartDataBuilder.buildOrderedInfluencesBarChartData(config, propName);
            } else {
                return null;
            }
        },
        buildStackedInfluencesBarChartData(config, propName) {
            if (config.dissectedProperties != null) {
                return ChartDataBuilder.buildStackedInfluencesBarChartData(config, propName);
            } else {
                return null;
            }
        },

        stackedOptions(selectedPropertyName) {
            const unit = this.systemProperties[selectedPropertyName].split(' ')[0];

            return {
                tooltips: {
                    mode: 'point',
                    intersect: false,
                },
                responsive: true,
                maintainAspectRatio: false,

                scales: {
                    xAxes: [{
                        ticks: {
                            callback: function(value, index, values) {
                                return value + unit;
                            }
                        },
                        stacked: true,
                    }],
                    yAxes: [{
                        stacked: true
                    }]
                }
            };
        },

        orderedInfluencesOptions(selectedPropertyName) {
            const unit = this.systemProperties[selectedPropertyName].split(' ')[0];

            return {
                tooltips: {
                    mode: 'index',
                    intersect: false
                },
                responsive: true,
                maintainAspectRatio: false,

                scales: {
                    xAxes: [{
                        ticks: {
                            callback: function(value, index, values) {
                                return value + unit;
                            }
                        },
                        stacked: true,
                    }],
                    yAxes: [{
                        stacked: true
                    }]
                },
            };
        },

        onSelectedConfigNameChanged(index) {
            this.selectedConfigs[index] = this.getSelectedConfig(this.selectedConfigNames[index]);
            this.selectedConfigs[index] = this.getSelectedConfig(this.selectedConfigNames[index]);
        },
        getSelectedConfig(configName) {
            return this.configurations.find(conf => {
                return conf.name === configName
            });
        },
        checkVisualizable(config) {
            if (config != null) {
                if (config.dissectedProperties != null) {
                    return true;
                }
            }
            return false;
        }
    }
}
</script>

<style scoped>

</style>

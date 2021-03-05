<template>
    <div>
        <b-container fluid>
            <b-row class="justify-content-center">
                <div class="mb-1">
                    <template v-for="prop in Object.keys(visibleProperties)">
                        <b-form-checkbox class="mx-1 mb-2 text-secondary" v-model="visibleProperties[prop]" :name="'check-button-' + prop"
                                         @input="updatePropertyVisibility($event, prop)" button button-variant="outline-primary" size="sm" >
                            {{ prop }}
                            <font-awesome-icon :icon="visibleProperties[prop]? 'eye' : 'eye-slash'" fixed-width/>
                        </b-form-checkbox>
                    </template>
                </div>
            </b-row>
            <b-row>
                <template v-for="(chartData, index) in chartDataArray">
                    <b-col v-if="visibleProperties[chartData.datasets[0].label]"
                        class="px-2 mb-3" cols="12" lg="4" md="6">
                        <b-card>
                            <div class="p-d-flex p-jc-between">
                                <div v-if="chartData.datasets[0].data.length === 1"
                                     class="d-flex align-items-center justify-content-center">
                                    <h5 class="p-m-0">{{chartData.datasets[0].label + ': ' + chartData.datasets[0].data[0].toFixed(2) + chartData.unit}}</h5>
                                </div>
                                <div v-else><h5 class="p-m-0">{{chartData.datasets[0].label}}</h5></div>
                                <div class="d-inline-flex">
                                    <b-button class="p-1" variant="link"
                                              @click="$emit('click-lens', chartData.datasets[0].label)">
                                        <font-awesome-icon icon="search" :style="{ color: '#6c757d' }" fixed-width/>
                                    </b-button>
                                    <b-dropdown class="no-outline" toggle-class="p-1 no-outline" variant="link" right no-caret>
                                        <template #button-content>
                                            <font-awesome-icon icon="cog" :style="{ color: '#6c757d' }" fixed-width/>
                                        </template>
                                        <b-dropdown-item-button @click="$emit('click-optimize', '', chartData.datasets[0].label)">
                                            <font-awesome-icon icon="compass" class="mr-1" :style="{ color: '#6c757d' }" fixed-width/>
                                            optimize
                                        </b-dropdown-item-button>
                                        <b-dropdown-item-button @click="$emit('del-config', index)">
                                            <font-awesome-icon icon="chart-line" class="mr-1" :style="{ color: '#6c757d' }" fixed-width/>
                                            find the best
                                        </b-dropdown-item-button>
                                    </b-dropdown>
                                </div>
                            </div>
                            <Chart type="horizontalBar" :data="chartData" :options="barChartsOptions(chartData)"/>
                        </b-card>
                    </b-col>
                </template>
            </b-row>
            <b-row>
                <b-col cols="12" lg="6" md="6"
                       class="px-2 mb-3">
                    <b-card>
                        <div class="p-d-flex p-jc-between">
                            <div><h5 class="p-m-0">normalized values</h5></div>
                        </div>
                        <Chart type="radar" :data="updateRadarData()" :options="radarOptions" />
                    </b-card>
                </b-col>
                <b-col cols="12" lg="6" md="6"
                       class="px-2 mb-3">
                    <b-card>
                        <div class="p-d-flex p-jc-between">
                            <div class="d-inline-flex"><h5 class="p-m-0">absolute differences</h5></div>
                        </div>
                        <div class="p-grid p-text-center">
                            <div class="p-col p-md-4">
                                <Knob :modelValue="deltaEnergy" :readonly="true" :min="-1500" :max="1500"
                                      valueColor="#9c27b0"/>
                                Property 1
                            </div>
                            <div class="p-col p-md-4">
                                <Knob :modelValue="deltaCpu" :readonly="true" :min="-100" :max="100"
                                      valueColor="#0288D1"/>
                                Property 2
                            </div>
                            <div class="p-col p-md-4">
                                <Knob :modelValue="deltaTime" :readonly="true" :min="-150" :max="150"
                                      valueColor="#D32F2F"/>
                                Property 3
                            </div>
                        </div>
                        <div class="p-d-flex p-jc-center">
                            <h4 class="p-m-0">config 1 - config 2</h4>
                        </div>
                    </b-card>
                </b-col>
            </b-row>
        </b-container>
    </div>
</template>

<script>
import Card from 'primevue/card';
import Chart from 'primevue/chart';
import Button from 'primevue/button';
import Knob from './Knob';

import { mapFields } from 'vuex-map-fields';
import { mapMutations } from 'vuex';

export default {
    name: "ChartArea",

    components: {
        Card,
        Chart,
        Button,
        Knob
    },

    props: [
        "chartDataArray",
        "radarData"
    ],

    watch: {
      radarData() {
          this.updatePropertyVisibility();
      }
    },

    data() {
        return {
            deltaEnergy: 2345-1245,
            deltaCpu: 321-243,
            deltaTime: 401-506,
            differenceEnergyData: {
                labels: ['Delta Energy'],
                datasets: [
                    {
                        label: 'config 1 - config 2',
                        backgroundColor: '#9c27b0',
                        data: [2345-1245]
                    },
                ]
            },
            differenceCPUData: {
                labels: ['Delta CPU'],
                datasets: [
                    {
                        label: 'config 1 - config 2',
                        backgroundColor: '#9c27b0',
                        data: [321-243]
                    },
                ]
            },
            differenceTimeData: {
                labels: ['Delta Time'],
                datasets: [
                    {
                        label: 'config 1 - config 2',
                        backgroundColor: '#9c27b0',
                        data: [401-506]
                    },
                ]
            },
            radarOptions: {
                scale: {
                    ticks: {
                        min: 0.0,
                        max: 1.0,
                        stepSize: 0.2,
                    }
                },
            },
        }
    },

    created() {
        // this.updateRadarData();
    },

    computed: {
        ...mapFields('uiLogicStore', ["visibleProperties"]),
    },

    methods: {
        ...mapMutations('uiLogicStore', ["setPropertyVisibility"]),

        updatePropertyVisibility(event, prop) {
            if (event != null && prop != null) {
                this.setPropertyVisibility({ property: prop, visible: event })
                this.updateRadarData();
            }
        },
        updateRadarData() {
            let mutableRadarData = JSON.parse(JSON.stringify(this.radarData));
            let removeDatapointFrom = [];

            for (const [index, propLabel] of this.radarData.labels.entries()) {
                if (!this.visibleProperties[propLabel]) {
                    removeDatapointFrom.push(index);
                }
            }

            if (removeDatapointFrom.length === 0) {
                return mutableRadarData;
            }

            mutableRadarData.labels = mutableRadarData.labels.filter(function(value, index) {
                return removeDatapointFrom.indexOf(index) === -1;
            });

            mutableRadarData.datasets.forEach(dataset => {
                dataset.data = dataset.data.filter(function(value, index) {
                    return removeDatapointFrom.indexOf(index) === -1;
                })
            });

            return mutableRadarData;
        },
        barChartsOptions(chartData) {
            const options = {
                title: {
                    display: true,
                    text: chartData.evalHint,
                    position: 'bottom'
                },
                responsive: true,
                maintainAspectRatio: true,
                hoverMode: 'index',
                stacked: false,
                scales: {
                    xAxes: [{
                        ticks: {
                            min: 0,
                            // Include a unit sign in the ticks
                            callback: function(value, index, values) {
                                return value + chartData.unit;
                            }
                        }
                    }]
                }
            };
            return options;
        },
        makeToast(variant = null) {
            this.$bvToast.toast('Function is not implemented yet.', {
                title: 'Unsupported function',
                variant: variant,
                solid: true
            })
        },
    }
}
</script>

<style scoped>
    .no-outline:focus,.no-outline:active {
        outline: none !important;
        box-shadow: none !important;
    }
</style>

<template>
    <b-container fluid>
        <b-row>
            <b-col v-for="(chartData, index) in chartDataArray" :key="index"
                   class="px-2 mb-3"
                   cols="12" lg="4" md="6">
                <b-card>
                    <div class="p-d-flex p-jc-between">
                        <div v-if="chartData.datasets[0].data.length === 1"
                             class="d-flex align-items-center justify-content-center">
                            <h5 class="p-m-0">{{chartData.datasets[0].label + ': ' + chartData.datasets[0].data[0].toFixed(2)}}</h5>
                        </div>
                        <div v-else><h5 class="p-m-0">{{chartData.datasets[0].label}}</h5></div>
                        <div class="d-inline-flex">
                            <b-button class="p-1" variant="link"
                                      @click="makeToast('info')">
                                <font-awesome-icon icon="search" :style="{ color: '#6c757d' }" fixed-width/>
                            </b-button>
                            <b-dropdown class="no-outline" toggle-class="p-1 no-outline" variant="link" right no-caret>
                                <template #button-content>
                                    <font-awesome-icon icon="cog" :style="{ color: '#6c757d' }" fixed-width/>
                                </template>
                                <b-dropdown-item-button @click="$emit('del-config', index)">
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
                    <Chart type="horizontalBar" :data="chartData" :options="options"/>
                </b-card>
            </b-col>
            <b-col cols="12" lg="6" md="6"
                   class="px-2 mb-3">
                <b-card>
                    <div class="p-d-flex p-jc-between">
                        <div><h5 class="p-m-0">normalized values</h5></div>
                    </div>
                    <Chart type="radar" :data="radarData" :options="radarOptions" />
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
</template>

<script>
import Card from 'primevue/card';
import Chart from 'primevue/chart';
import Button from 'primevue/button';
import Knob from './Knob';



export default {
    name: "ChartArea",
    components: {
        Card,
        Chart,
        Button,
        Knob
    },
    props: [
        "configurationProperties",
        "chartDataArray",
        "radarData"
    ],
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
            options: {
                responsive: true,
                maintainAspectRatio: true,
                hoverMode: 'index',
                stacked: false,
                scales: {
                    xAxes: [{
                        ticks: {
                            min: 0
                        }
                    }]
                }
            },
            radarOptions: {
                scale: {
                },
                ticks: {
                    min: 0.0,
                    max: 1.0,
                }
            },
        }
    },
    methods: {
        makeToast(variant = null) {
            this.$bvToast.toast('Function is not implemented yet.', {
                title: 'Unsupported function',
                variant: variant,
                solid: true
            })
        }
    }

}
</script>

<style scoped>
    .no-outline:focus,.no-outline:active {
        outline: none !important;
        box-shadow: none !important;
    }
</style>

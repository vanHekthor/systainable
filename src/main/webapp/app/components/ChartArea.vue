<template>
    <div class="p-p-3">
        <div class="p-grid">
            <div v-for="(chartData, index) in chartDataArray" :key="index"
                 class="p-col-12 p-md-6 p-lg-4">
                <Card>
                    <template #title>
                        <div class="p-d-flex p-jc-between">
                            <div v-if="chartData.datasets[0].data.length === 1">
                                <h5 class="p-m-0">{{chartData.datasets[0].label + ': ' + chartData.datasets[0].data[0].toFixed(2)}}</h5>
                            </div>
                            <div v-else><h5 class="p-m-0">{{chartData.datasets[0].label}}</h5></div>
                            <div>
                                <Button icon="pi pi-filter"
                                        class="p-button-rounded p-button-text p-button-plain"
                                        @click="makeToast('info')"/>
                                <Button icon="pi pi-cog"
                                        class="p-button-rounded p-button-secondary p-button-text"
                                        @click="makeToast('info')"/>
                            </div>
                        </div>
                    </template>
                    <template #content>
                        <Chart type="horizontalBar" :data="chartData" :options="options"/>
                    </template>
                </Card>
            </div>
            <div class="p-col-12 p-md-6 p-lg-6">
                <Card>
                    <template #title>
                        <div class="p-d-flex p-jc-between">
                            <div><h5 class="p-m-0">normalized values</h5></div>
                            <div>
                                <Button icon="pi pi-filter"
                                        class="p-button-rounded p-button-text p-button-plain"
                                        @click="makeToast('info')"/>
                                <Button type="button" icon="pi pi-cog"
                                        class="p-button-rounded p-button-secondary p-button-text"
                                        @click="makeToast('info')"/>
                            </div>
                        </div>
                    </template>
                    <template #content>
                        <Chart type="radar" :data="radarData" :options="radarOptions" />
                    </template>
                </Card>
            </div>
            <div class="p-col-12 p-md-6 p-lg-6">
                <Card>
                    <template #title>
                        <div class="p-d-flex p-jc-between">
                            <div><h5 class="p-m-0">absolute differences</h5></div>
                            <div>
                                <Button icon="pi pi-filter"
                                        class="p-button-rounded p-button-text p-button-plain"
                                        @click="makeToast('info')"/>
                                <Button type="button" icon="pi pi-cog"
                                        class="p-button-rounded p-button-secondary p-button-text"
                                        @click="makeToast('info')"/>
                            </div>
                        </div>
                    </template>
                    <template #content>
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
                    </template>
                    <template #footer>
                        <div class="p-d-flex p-jc-center">
                            <h4 class="p-m-0">config 1 - config 2</h4>
                        </div>

                    </template>
                </Card>
            </div>
        </div>
    </div>
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

</style>

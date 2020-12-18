<template>
    <div class="p-p-3">
        <div class="p-grid">
            <div class="p-col-12 p-md-6 p-lg-4">
                <Card>
                    <template #title>
                        <div class="p-d-flex p-jc-between">
                            <div><h5 class="p-m-0">Property 1</h5></div>
                            <div>
                                <Button icon="pi pi-filter"
                                        class="p-button-rounded p-button-text p-button-plain" />
                                <Button type="button" icon="pi pi-cog"
                                        class="p-button-rounded p-button-secondary p-button-text"/>
                            </div>
                        </div>
                    </template>
                    <template #content>
                        <Chart type="horizontalBar" :data="energyData" :options="options"/>
                    </template>
                </Card>
            </div>
            <div class="p-col-12 p-md-6 p-lg-4">
                <Card>
                    <template #title>
                        <div class="p-d-flex p-jc-between">
                            <div><h5 class="p-m-0">Property 2</h5></div>
                            <div>
                                <Button icon="pi pi-filter"
                                        class="p-button-rounded p-button-text p-button-plain" />
                                <Button type="button" icon="pi pi-cog"
                                        class="p-button-rounded p-button-secondary p-button-text"/>
                            </div>
                        </div>
                    </template>
                    <template #content>
                        <Chart type="horizontalBar" :data="cpuData" :options="options"/>
                    </template>
                </Card>
            </div>
            <div class="p-col-12 p-md-6 p-lg-4">
                <Card>
                    <template #title>
                        <div class="p-d-flex p-jc-between">
                            <div><h5 class="p-m-0">Property 3</h5></div>
                            <div>
                                <Button icon="pi pi-filter"
                                        class="p-button-rounded p-button-text p-button-plain" />
                                <Button type="button" icon="pi pi-cog"
                                        class="p-button-rounded p-button-secondary p-button-text"/>
                            </div>
                        </div>
                    </template>
                    <template #content>
                        <Chart type="horizontalBar" :data="timeData" :options="options"/>
                    </template>
                </Card>
            </div>
            <div class="p-col-12 p-md-6 p-lg-6">
                <Card>
                    <template #title>
                        <div class="p-d-flex p-jc-between">
                            <div><h5 class="p-m-0">Normalized Values</h5></div>
                            <div>
                                <Button icon="pi pi-filter"
                                        class="p-button-rounded p-button-text p-button-plain" />
                                <Button type="button" icon="pi pi-cog"
                                        class="p-button-rounded p-button-secondary p-button-text"/>
                            </div>
                        </div>
                    </template>
                    <template #content>
                        <Chart type="radar" :data="chartData" :options="radarOptions" />
                    </template>
                </Card>
            </div>
            <div class="p-col-12 p-md-6 p-lg-6">
                <Card>
                    <template #title>
                        <div class="p-d-flex p-jc-between">
                            <div><h5 class="p-m-0">Absolute Differences</h5></div>
                            <div>
                                <Button icon="pi pi-filter"
                                        class="p-button-rounded p-button-text p-button-plain" />
                                <Button type="button" icon="pi pi-cog"
                                        class="p-button-rounded p-button-secondary p-button-text"/>
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
    data() {
        return {
            energyData: {
                labels: ['Config 1', 'Config 2'],
                datasets: [
                    {
                        label: 'Property 1',
                        backgroundColor: '#9c27b0',
                        data: [2345, 1245]
                    },
                ]
            },
            cpuData: {
                labels: ['config 1', 'config 2'],
                datasets: [
                    {
                        label: 'Property 2',
                        backgroundColor: '#0288D1',
                        data: [321, 243]
                    },
                ]
            },
            timeData: {
                labels: ['config 1', 'config 2'],
                datasets: [
                    {
                        label: 'Property 3',
                        backgroundColor: '#D32F2F',
                        data: [401, 506]
                    },
                ]
            },
            deltaEnergy: 2345-1245,
            deltaCpu: 321-243,
            deltaTime: 401-506,
            chartData: {
                labels: ['Property 1', 'Property 2', 'Property 3'],
                datasets: [
                    {
                        label: 'config 1',
                        backgroundColor: 'rgba(0,255,60,0.2)',
                        borderColor: 'rgba(61,164,91,0.7)',
                        pointBackgroundColor: 'rgb(32,171,65)',
                        pointBorderColor: '#fff',
                        pointHoverBackgroundColor: 'rgb(27,215,78)',
                        pointHoverBorderColor: 'rgba(27,215,78,1)',
                        data: [1, 1, 401/506]
                    },
                    {
                        label: 'config 2',
                        backgroundColor: 'rgb(255,102,0,0.5)',
                        borderColor: 'rgb(255,102,0)',
                        pointBackgroundColor: 'rgba(255,102,0,1)',
                        pointBorderColor: '#fff',
                        pointHoverBackgroundColor: 'rgba(255,102,0,1)',
                        pointHoverBorderColor: 'rgba(255,99,132,1)',
                        data: [1245/2345, 243/321, 1]
                    }
                ]
            },
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
                    max: 1.0
                }
            },
        }
    }
}
</script>

<style scoped>

</style>

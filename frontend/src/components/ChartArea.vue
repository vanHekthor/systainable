<template>
  <div class="p-mt-4">
    <div class="p-d-flex p-jc-center">
      <div class="p-p-3">
        <Button type="button" label="Energy" class="p-button-help p-d-block p-mx-auto" />
      </div>
      <div class="p-p-3">
        <Button type="button" label="CPU" class="p-button-info p-d-block p-mx-auto" />
      </div>
      <div class="p-p-3">
        <Button type="button" label="Time" class="p-button-danger p-d-block p-mx-auto" />
      </div>
    </div>

    <div class="p-grid">
      <div class="card p-col-12 p-md-6 p-lg-4">
        <Card>
          <template #content>
            <Chart type="bar" :data="energyData" :options="options"/>
          </template>
        </Card>
      </div>
      <div class="card p-col-12 p-md-6 p-lg-4">
        <Card>
          <template #content>
            <Chart type="bar" :data="cpuData" :options="options"/>
          </template>
        </Card>
      </div>
      <div class="card p-col-12 p-md-6 p-lg-4">
        <Card>
          <template #content>
            <Chart type="bar" :data="timeData" :options="options"/>
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


export default {
  name: "ChartArea",
  components: {
    Card,
    Chart,
    Button
  },
  data() {
    return {
      energyData: {
        labels: ['config 1', 'config 2'],
        datasets: [
          {
            label: 'Energy',
            backgroundColor: '#9C27B0',
            data: [2345, 1245]
          },
        ]
      },
      cpuData: {
        labels: ['config 1', 'config 2'],
        datasets: [
          {
            label: 'CPU',
            backgroundColor: '#0288D1',
            data: [321, 243]
          },
        ]
      },
      timeData: {
        labels: ['config 1', 'config 2'],
        datasets: [
          {
            label: 'Time',
            backgroundColor: '#D32F2F',
            data: [401, 506]
          },
        ]
      },
      chartData: {
        labels: ['Energy', 'CPU', 'Time'],
        datasets: [
          {
            label: 'first configuration',
            backgroundColor: 'rgba(179,181,198,0.2)',
            borderColor: 'rgba(179,181,198,1)',
            pointBackgroundColor: 'rgba(179,181,198,1)',
            pointBorderColor: '#fff',
            pointHoverBackgroundColor: '#fff',
            pointHoverBorderColor: 'rgba(179,181,198,1)',
            data: [0.5, 1, 0.35]
          },
          {
            label: 'second configuration',
            backgroundColor: 'rgba(255,99,132,0.2)',
            borderColor: 'rgba(255,99,132,1)',
            pointBackgroundColor: 'rgba(255,99,132,1)',
            pointBorderColor: '#fff',
            pointHoverBackgroundColor: '#fff',
            pointHoverBorderColor: 'rgba(255,99,132,1)',
            data: [0.3, 0.63, 0.4]
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: true,
        hoverMode: 'index',
        stacked: false,
        scales: {
          yAxes: [{
            ticks: {
              min: 0
            }
          }]
        }
      }
    }
  }
}
</script>

<style scoped>
  html {
    font-size: 12px;
  }
</style>
<template>
  <div class="p-grid">
    <div class="p-col">
      <div class="p-shadow-1 p-p-3">
        <Button type="button" label="Energy: 123" class="p-button-help p-d-block p-mx-auto" />
      </div>
    </div>
    <div class="p-col">
      <div class="p-shadow-1 p-p-3">
        <Button type="button" label="CPU: 231" class="p-button-info p-d-block p-mx-auto" />
      </div>
    </div>
    <div class="p-col">
      <div class="p-shadow-1 p-p-3">
            <Button type="button" label="Time: 321" class="p-button-danger p-d-block p-mx-auto" />
      </div>
    </div>
  </div>
  <div class="p-grid">
    <div class="p-col-12 p-md-6 p-lg-6">
      <Card>
        <template #content>
          <Chart type="radar" :data="chartData" />
        </template>
      </Card>
    </div>
    <div class="card p-col-12 p-md-6 p-lg-6">
      <Card>
        <template #content>
          <Chart type="bar" :data="basicData" />
        </template>
      </Card>
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
      basicData: {
        labels: ['Energy', 'CPU', 'Time'],
        datasets: [
          {
            label: 'My First dataset',
            backgroundColor: '#42A5F5',
            data: [65, 59, 80]
          },
          {
            label: 'My Second dataset',
            backgroundColor: '#FFA726',
            data: [0.5, 89.3, 5]
          }
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
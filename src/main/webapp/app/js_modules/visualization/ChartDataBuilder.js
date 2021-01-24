const backgroundColors = ['#9c27b0', '#0288D1', '#D32F2F'];
const radarColors = {
  backgroundColor: ['rgb(255,102,0,0.5)', 'rgba(0,255,60,0.2)'],
  borderColor: ['rgb(255,102,0)', 'rgba(61,164,91,0.7)'],
  pointBackgroundColor: ['rgba(255,102,0,1)', 'rgb(32,171,65)'],
  pointBorderColor: ['#fff', '#fff'],
  pointHoverBackgroundColor: ['rgba(255,102,0,1)', 'rgb(27,215,78)'],
  pointHoverBorderColor: ['rgba(255,99,132,1)', 'rgba(27,215,78,1)'],
};

export default {
  buildBarChartData: function buildBarChartData(configNames, propertyMaps) {
    let configLabels = configNames;
    let propertyLabels = [...propertyMaps[0].keys()];

    let label = '';
    let chartDataArray = [];
    let i = 0;
    for (label of propertyLabels) {
      let values = [];
      let propertyMap;
      for (propertyMap of propertyMaps) {
        values.push(propertyMap.get(label));
      }
      let chartData = {
        labels: configLabels,
        datasets: [
          {
            label: label,
            backgroundColor: backgroundColors[i % 3],
            data: values,
          },
        ],
      };
      chartDataArray.push(chartData);
      i++;
    }

    return chartDataArray;
  },

  buildRadarData: function buildRadarData(configNames, propertyMaps) {
    let configLabels = configNames;
    let label = '';

    //normalize values
    for (label of [...propertyMaps[0].keys()]) {
      let propertyValues = [];
      let propertyMap;
      for (propertyMap of propertyMaps) {
        propertyValues.push(propertyMap.get(label));
      }
      let maxVal = Math.max(...propertyValues);
      for (propertyMap of propertyMaps) {
        propertyMap.set(label, propertyMap.get(label) / maxVal);
      }
    }

    let radarData = {
      labels: [...propertyMaps[0].keys()],
      datasets: [],
    };
    let i = 0;
    for (label of configLabels) {
      let dataset = {
        label: label,
        backgroundColor: radarColors.backgroundColor[i % 2],
        borderColor: radarColors.borderColor[i % 2],
        pointBackgroundColor: radarColors.pointBackgroundColor[i % 2],
        pointBorderColor: radarColors.pointBorderColor[i % 2],
        pointHoverBackgroundColor: radarColors.pointHoverBackgroundColor[i % 2],
        pointHoverBorderColor: radarColors.pointHoverBorderColor[i % 2],
        data: [...propertyMaps[i].values()],
      };
      radarData.datasets.push(dataset);
      i++;
    }
    return radarData;
  },
};

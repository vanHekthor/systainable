const backgroundColors = ['#9c27b0', '#0288D1', '#D32F2F'];
const radarColors = {
  backgroundColor: ['rgb(255,102,0,0.5)', 'rgba(0,255,60,0.2)'],
  borderColor: ['rgb(255,102,0)', 'rgba(61,164,91,0.7)'],
  pointBackgroundColor: ['rgba(255,102,0,1)', 'rgb(32,171,65)'],
  pointBorderColor: ['#fff', '#fff'],
  pointHoverBackgroundColor: ['rgba(255,102,0,1)', 'rgb(27,215,78)'],
  pointHoverBorderColor: ['rgba(255,99,132,1)', 'rgba(27,215,78,1)'],
};

/**
 * This function generates a evaluation hint based on the property attributes that shows if it is more desirable to have a low or a high property value.
 * @param hintString String that should be either '&lt;' or '&gt;'
 * @returns {string} Hint that shows if higher or lower property values are better
 */
function getPropEvalHint(hintString) {
  let evalHint = '';

  if (hintString === '<') {
    evalHint = '(the lower the better)';
  } else if (hintString === '>') {
    evalHint = '(the higher the better)';
  } else {
    throw Error("Wrong string for property evaluation hint. Valid strings: '&lt;' or '&gt;'");
  }

  return evalHint;
}

export default {
  /**
   * This method builds the chart data for the bar charts that show property values.
   * @param configNames Names of the configurations shown in the charts
   * @param propertyAttributes Property attributes meaning unit and if lower or higher values are better (displayed as '&lt;' or '&gt;')
   * @param propertyMaps Maps with property names as keys and property values as values
   * @returns {[]} Array of bar chart data objects
   */
  buildBarChartData: function buildBarChartData(configNames, propertyAttributes, propertyMaps) {
    let configLabels = configNames;
    let propertyLabels = [...propertyMaps[0].keys()];

    let label = '';
    let chartDataArray = [];
    let i = 0;
    for (label of propertyLabels) {
      let values = [];
      let propertyMap;
      const unit = propertyAttributes[label].split(' ')[0];

      let evalHint = '';
      try {
        evalHint = getPropEvalHint(propertyAttributes[label].split(' ')[1]);
      } catch (e) {
        console.log(e.message);
      }

      for (propertyMap of propertyMaps) {
        values.push(propertyMap.get(label));
      }
      let chartData = {
        labels: configLabels,
        unit: unit,
        evalHint: evalHint,
        datasets: [
          {
            label: label + `\xa0[${unit}]`,
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

  /**
   * This method builds the chart data for the radar chart that shows normalized property values.
   * @param configNames configNames Names of the configurations shown in the charts
   * @param propertyAttributes Property attributes, only property units are relevant here
   * @param propertyMaps Maps with property names as keys and property values as values
   * @returns {{datasets: [], labels: *[]}} Object with labels for each property and the datasets corresponding to the configurations
   */
  buildRadarData: function buildRadarData(configNames, propertyAttributes, propertyMaps) {
    // normalize values
    for (let label of [...propertyMaps[0].keys()]) {
      let propertyValues = [];
      let propertyMap;

      for (propertyMap of propertyMaps) {
        propertyValues.push(propertyMap.get(label));
      }

      let maxVal = Math.max(...propertyValues);
      const unit = propertyAttributes[label].split(' ')[0];
      for (propertyMap of propertyMaps) {
        propertyMap.set(label + `\xa0[${unit}]`, propertyMap.get(label) / maxVal);
        propertyMap.delete(label);
      }
    }

    let radarData = {
      labels: [...propertyMaps[0].keys()],
      datasets: [],
    };

    let i = 0;
    for (let configName of configNames) {
      let dataset = {
        label: configName,
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

import colorPalette from '@/js_modules/visualization/colorPalette';
import underscoreUtil from '@/js_modules/util/underscoreUtil';

const INFLUENCE_LABEL = 0;
const INFLUENCE_VALUE = 1;

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

/**
 * This function extracts all influences (i.e. features and feature interactions) on a property which exist for a configuration out of that configuration and
 * then constructs an array containing pairs of influence labels and influence values as elements. For example [["feature1", 10.0], ["feature2 + feature3", -5.0],
 * ["feature1 + feature3", -2.0]]. <br>
 * The array is sorted by influence value in descending order.
 * @param config Already evaluated configuration containing the influence information.
 * @param propertyName Property for which the influences shall be extracted.
 * @returns {[]} Array containing pairs of influence labels and influence values [<label>, <value>] as elements.
 */
function constructInfluenceData(config, propertyName) {
  const propName = underscoreUtil.replaceNonBreakSpaces(propertyName);

  let influences = Object.keys(config.dissectedProperties).map(function (key) {
    return [config.dissectedProperties[key].features.join(' + '), config.dissectedProperties[key].properties[propName]];
  });

  influences = influences.filter(influence => {
    return influence[INFLUENCE_VALUE] !== 0;
  });

  influences.sort(function (a, b) {
    return b[1] - a[1];
  });

  return influences;
}

/**
 * This function constructs the dataset for each relevant influence which is needed for visualizing it in a stacked bar chart and returns an array of datasets.
 * @param config Already evaluated configuration containing the influence information
 * @param propertyName Property for which the stacked bar chart shall be made
 * @returns {[]} Array containing influence datasets
 */
function constructStackedInfluencesDatasets(config, propertyName) {
  let stackedInfluencesDatasets = [];
  let addedAllPosInfluences = false;
  let addedInfluenceSum = false;

  const sortedInfluences = constructInfluenceData(config, propertyName);

  let posInfluencesCount = 0;
  for (let influence of sortedInfluences) {
    if (influence[INFLUENCE_VALUE] > 0) {
      posInfluencesCount++;
    } else {
      // point reached where all pos. influences were counted
      break;
    }
  }

  const posInfluenceColors = colorPalette.posInfluencesColorArray(posInfluencesCount);
  const negInfluenceColors = colorPalette.negInfluencesColorArray(sortedInfluences.length - posInfluencesCount);

  // generating a dataset for each influence in sortedInfluences plus the dataset for the sum of influences
  for (let [index, influence] of sortedInfluences.entries()) {
    let influenceDataset = null;

    if (influence[INFLUENCE_VALUE] > 0) {
      // positive influence dataset
      influenceDataset = {
        type: 'horizontalBar',
        label: influence[INFLUENCE_LABEL],
        backgroundColor: colorPalette.setOpacity(posInfluenceColors[index], 0.7),
        borderWidth: 2,
        borderColor: posInfluenceColors[index],
        hoverBackgroundColor: colorPalette.setOpacity(posInfluenceColors[index], 0.7),
        data: [influence[INFLUENCE_VALUE]],
        stack: 1,
      };
    } else {
      addedAllPosInfluences = true;

      if (!addedInfluenceSum) {
        // dataset for sum of all influences
        const influenceSumDataset = {
          type: 'horizontalBar',
          label: 'Sum of influences',
          backgroundColor: colorPalette.influenceSumColor,
          data: [config.properties[propertyName]],
          stack: 2,
        };
        stackedInfluencesDatasets.push(influenceSumDataset);

        addedInfluenceSum = true;
      }
    }

    // influences with value == 0 were already filtered out in constructInfluenceData()

    if (addedAllPosInfluences) {
      // relative position in array part with neg. influences
      const relativeIndex = index - posInfluencesCount;

      // reversed index for going through neg. influences in reversed order
      const reversedIndex = sortedInfluences.length - 1 - relativeIndex;

      if (sortedInfluences[reversedIndex][INFLUENCE_VALUE] < 0) {
        // negative influence dataset
        influenceDataset = {
          type: 'horizontalBar',
          label: sortedInfluences[reversedIndex][INFLUENCE_LABEL],
          backgroundColor: colorPalette.setOpacity(negInfluenceColors[relativeIndex], 0.7),
          borderWidth: 2,
          borderColor: negInfluenceColors[index - posInfluencesCount],
          hoverBackgroundColor: colorPalette.setOpacity(negInfluenceColors[relativeIndex], 0.7),
          data: [sortedInfluences[reversedIndex][INFLUENCE_VALUE] * -1],
          stack: 2,
        };
      }
    }

    if (influenceDataset != null) {
      stackedInfluencesDatasets.push(influenceDataset);
    }
  }
  // if only positive influences exist the sum was not added yet therefore:
  if (!addedInfluenceSum) {
    // dataset for sum of all influences
    const influenceSumDataset = {
      type: 'horizontalBar',
      label: 'Sum of influences',
      backgroundColor: colorPalette.influenceSumColor,
      data: [config.properties[propertyName]],
      stack: 2,
    };
    stackedInfluencesDatasets.push(influenceSumDataset);
  }

  return stackedInfluencesDatasets;
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
    let color = colorPalette.colorArrayChart(propertyLabels.length);

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
            label: label,
            backgroundColor: color[i],
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
      for (propertyMap of propertyMaps) {
        propertyMap.set(label, propertyMap.get(label) / maxVal);
      }
    }

    let radarData = {
      labels: [...propertyMaps[0].keys()],
      datasets: [],
    };

    let i = 0;
    let radarColors = colorPalette.colorArrayRadar(configNames.length);
    for (let configName of configNames) {
      let dataset = {
        label: configName,
        backgroundColor: radarColors.backgroundColor[i],
        borderColor: radarColors.borderColor[i],
        pointBackgroundColor: radarColors.pointBackgroundColor[i],
        pointBorderColor: radarColors.pointBorderColor[i],
        pointHoverBackgroundColor: radarColors.pointHoverBackgroundColor[i],
        pointHoverBorderColor: radarColors.pointHoverBorderColor[i],
        data: [...propertyMaps[i].values()],
      };
      radarData.datasets.push(dataset);
      i++;
    }
    return radarData;
  },

  /**
   * This method builds the chart data for the not-stacked bar chart in the influence area that displays influences on a property as bars in descending order.
   * @param config Already evaluated configuration containing the influence information.
   * @param propertyName Property for which the influences shall be extracted.
   * @returns {{datasets: [{backgroundColor: string, data: [], label: string, type: string}], labels: []}} Bar chart data object
   */
  buildOrderedInfluencesBarChartData: function (config, propertyName) {
    let influences = constructInfluenceData(config, propertyName);

    let influenceLabels = [];
    let influenceValues = [];

    for (let influence of influences) {
      if (Math.abs(influence[INFLUENCE_VALUE]) > 0) {
        influenceLabels.push(influence[INFLUENCE_LABEL]);
        influenceValues.push(influence[INFLUENCE_VALUE]);
      }
    }

    let chartData = {
      labels: influenceLabels,
      // unit: unit,
      // evalHint: evalHint,
      datasets: [
        {
          type: 'horizontalBar',
          label: `effect on ${propertyName}`,
          backgroundColor: colorPalette.setOpacity(colorPalette.influenceSumColor, 0.67),
          hoverBackgroundColor: colorPalette.influenceSumColor,
          data: influenceValues,
        },
      ],
    };

    return chartData;
  },

  /**
   * This method builds the chart data for the stacked bar chart in the influence view that displays influences on a property separated into one stack with positive influences and
   * another stack containing the sum of all influences and negative influences.
   * @param config Already evaluated configuration containing the influence information.
   * @param propertyName Property for which the influences shall be extracted.
   * @returns {{datasets: [], labels: [*]}} Stacked bar chart data object
   */
  buildStackedInfluencesBarChartData: function (config, propertyName) {
    const stackedInfluencesDatasets = constructStackedInfluencesDatasets(config, propertyName);

    let stackedData = {
      labels: [config.name],
      datasets: stackedInfluencesDatasets,
    };

    return stackedData;
  },
};

import chroma from 'chroma-js';

export default {
  colorArrayChart: function colorArrayChart(propLength) {
    let count = propLength.length;

    return chroma.scale(['#0288D1', '#D32F2F']).mode('lch').colors(count);
  },

  colorArrayRadar: function colorArrayRadar(propLength) {
    let count = propLength.length;
    return {
      backgroundColor: chroma.scale(['rgba(0, 0, 0, 0)']).mode('lch').colors(count),
      borderColor: chroma.scale(['#FF6600', '#00FF3C', '#0288D1']).mode('lch').colors(count),
      pointBackgroundColor: chroma.scale(['#FF6600', '#00FF3C', '#0288D1']).mode('lch').colors(count),
      pointBorderColor: chroma.scale(['#fff']).mode('lch').colors(count),
      pointHoverBackgroundColor: chroma.scale(['#FF6600', '#00FF3C', '#0288D1']).mode('lch').colors(count),
      pointHoverBorderColor: chroma.scale(['#FF6384', '#00FF3C']).mode('lch').colors(count),
    };
  },
};

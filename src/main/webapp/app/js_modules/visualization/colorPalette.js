import chroma from 'chroma-js';

export default {
  colorArrayChart: function colorArrayChart(colorAmount) {
    return chroma.scale(['#0288D1', '#D32F2F']).mode('lch').colors(colorAmount);
  },

  colorArrayRadar: function colorArrayRadar(colorAmount) {
    return {
      backgroundColor: chroma.scale(['rgba(0, 0, 0, 0)']).mode('lch').colors(colorAmount),
      borderColor: chroma.scale(['#FF6600', '#00FF3C', '#0288D1']).mode('lch').colors(colorAmount),
      pointBackgroundColor: chroma.scale(['#FF6600', '#00FF3C', '#0288D1']).mode('lch').colors(colorAmount),
      pointBorderColor: chroma.scale(['#fff']).mode('lch').colors(colorAmount),
      pointHoverBackgroundColor: chroma.scale(['#FF6600', '#00FF3C', '#0288D1']).mode('lch').colors(colorAmount),
      pointHoverBorderColor: chroma.scale(['#FF6384', '#00FF3C']).mode('lch').colors(colorAmount),
    };
  },
};

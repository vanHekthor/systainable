import chroma from 'chroma-js';

export default {
  influenceSumColor: '#800080',
  negInfluenceColor: '#e6550d',
  posInfluenceColor: '#005dab',

  colorArrayChart: function (colorAmount) {
    return chroma.scale(['#0288D1', '#D32F2F']).mode('lch').colors(colorAmount);
  },

  colorArrayRadar: function (colorAmount) {
    return {
      backgroundColor: chroma.scale(['rgba(0, 0, 0, 0)']).mode('lch').colors(colorAmount),
      borderColor: chroma.scale(['#FF6600', '#00FF3C', '#0288D1']).mode('lch').colors(colorAmount),
      pointBackgroundColor: chroma.scale(['#FF6600', '#00FF3C', '#0288D1']).mode('lch').colors(colorAmount),
      pointBorderColor: chroma.scale(['#fff']).mode('lch').colors(colorAmount),
      pointHoverBackgroundColor: chroma.scale(['#FF6600', '#00FF3C', '#0288D1']).mode('lch').colors(colorAmount),
      pointHoverBorderColor: chroma.scale(['#FF6384', '#00FF3C']).mode('lch').colors(colorAmount),
    };
  },

  posInfluencesColorArray: function (colorAmount) {
    return chroma.scale(['#005dab', '#78b6f5', '#bddbff']).mode('lch').colors(colorAmount);
  },

  negInfluencesColorArray: function (colorAmount) {
    return chroma.scale(['#e6550d', '#fd976b', '#ffe0cc']).mode('lch').colors(colorAmount);
  },

  setOpacity: function (color, opacity) {
    return chroma(color).alpha(opacity);
  },
};

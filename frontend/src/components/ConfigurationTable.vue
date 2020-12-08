<template>
  <div>
      <table class="configuration-table">
        <thead>
          <tr>
            <th/>
            <th v-for="(feature, index) in configurationFeatures" :key="index">
              {{feature.name}}
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(configuration, configIndex) in configurations" :key="configIndex">
            <th>{{configuration.name}}</th>
            <td v-for="(feature, featureIndex) in configuration.features" :key="featureIndex" >
              <label>
                <input type="checkbox" :checked="feature" @change="$emit('update-feature', configIndex, featureIndex)">
              </label>
            </td>
          </tr>
        </tbody>
      </table>
  </div>
</template>

<script>
export default {
  name: "ConfigurationTable",
  components: {},
  props: ["configurationFeatures", "configurations"],
}
</script>

<style scoped>
  .configuration-table {
    font-family: 'Arial',serif;
    font-size: small;
    margin: 25px auto;
    border-collapse: collapse;
    border: 1px solid #eee;
    border-bottom: 2px solid #3771c8;
    box-shadow: 0 0 20px rgba(0, 0, 0, 0.1),
                0 10px 20px rgba(0, 0, 0, 0.05),
                0 20px 20px rgba(0, 0, 0, 0.05),
                0 30px 20px rgba(0, 0, 0, 0.05);
  }
  .configuration-table th {
    border: 1px solid #eee;
    background: #3771c8;
    color: #fff;
    padding: 10px;
  }
  tr:hover {
    background: #f4f4f4;
  }
  td {
    border: 1px solid #eee;
    padding: 10px;
  }
  td:hover {
    background: #bbd3ff;
  }
</style>
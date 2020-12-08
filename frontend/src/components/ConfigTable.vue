<template>
  <DataTable rowHover="true" autoLayout="true" :value="configurations" editMode="cell" @cell-edit-complete="onCellEditComplete" class="editable-cells-table">
    <Column v-for="feature of configurationFeatures" :field="feature.name" :header="feature.name"
            :key="feature.name" bodyStyle="text-align:center">
      <template v-if="feature.name == 'name'" #editor="slotProps">
        <InputText :modelValue="slotProps.data[feature.name]"
                   @update:modelValue="$emit('update-config-name', slotProps.index,$event)"
                   class="p-inputtext" />
<!--        <p v-if="feature.name == 'name'">{{slotProps.data.name}}</p>-->
      </template>
      <template v-else #body="slotProps">
        <label>
          <input v-if="feature.name != 'name'" type="checkbox" :checked="slotProps.data[feature.name]"
                 @change="$emit('update-feature', slotProps.index, feature.name)"/>
        </label>
      </template>
    </Column>
  </DataTable>
</template>

<script>
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import InputText from 'primevue/inputtext';


export default {
  name: "ConfigTable",
  components: {
    DataTable,
    Column,
    InputText
  },
  props: ["configurationFeatures", "configurations"],

  data() {
    return {
      columns: null,
      editingCellRows: {},
      editingRows: [],
    }
  },

  created() {
    let feature;
    let columns = []
    for (feature of this.configurationFeatures) {
      columns.push({field: feature.name, header: feature.name});
      console.log(feature.name);
    }
    this.columns = columns;
  },

  methods: {
    onCellEditComplete(event) {
      if (!this.editingCellRows[event.index]) {
        return;
      }

      const editingCellValue = this.editingCellRows[event.index][event.field];

      switch (event.field) {
        case 'quantity':
        case 'price':
          if (this.isPositiveInteger(editingCellValue))
            this.products2[event.index] = {...this.editingCellRows[event.index]};
          else
            event.preventDefault();
          break;

        default:
          if (editingCellValue.trim().length > 0)
            this.products2[event.index] = {...this.editingCellRows[event.index]};
          else
            event.preventDefault();
          break;
      }
    },
    onCellEdit(newValue, props) {
      console.log('haha' + newValue);
      if (!this.editingCellRows[props.index]) {
        this.editingCellRows[props.index] = {...props.data};
      }

      this.editingCellRows[props.index][props.column.props.field] = newValue;
    }
  },
}
</script>

<style scoped>

</style>
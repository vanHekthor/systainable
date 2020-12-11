<template>
  <Panel header="Configurations" v-if="configurationFeatures.length > 0">
    <DataTable :rowHover="true" :autoLayout="true" :value="configurations"
               editMode="cell" @cell-edit-complete="onCellEditComplete" class="editable-cells-table">
      <Column v-for="feature of configurationFeatures" :field="feature.name" :header="feature.name"
              :key="feature.name" bodyStyle="">
        <template v-if="feature.name == 'name'" #editor="slotProps">
          <div class="p-grid" @mouseover="hover=true" @mouseleave="hover=false">

            <InputText :modelValue="slotProps.data[feature.name]"
                       @update:modelValue="$emit('update-config-name', slotProps.index,$event)"
                       class="p-inputtext-sm" />
            <Button label="Delete" icon="pi pi-times" class="p-button-danger p-button-sm p-mt-2"
                    @click="$emit('del-config', slotProps.index)"/>
          </div>
        </template>
        <template v-else #body="slotProps">
          <label>
            <input type="checkbox" :checked="slotProps.data[feature.name]"
                   @change="$emit('update-feature', slotProps.index, feature.name)"/>
          </label>
        </template>
      </Column>
    </DataTable>
  </Panel>
  <Panel v-else header="Empty">
    Please select a software system.
  </Panel>
</template>

<script>
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import InputText from 'primevue/inputtext';
import Panel from 'primevue/panel';
import Button from 'primevue/button';




export default {
  name: "ConfigTable",
  components: {
    DataTable,
    Column,
    InputText,
    Panel,
    Button
  },
  props: ["configurationFeatures", "configurations"],

  data() {
    return {
      columns: null,
      editingCellRows: {},
      editingRows: [],
      hover: false,
    }
  },

  created() {
  },

  methods: {
    onCellEditComplete(event) {
      if (!this.editingCellRows[event.index]) {
        return;
      }
    },
  },
}
</script>

<style scoped>

</style>
<template>
    <div class="p-px-3">
        <Panel v-if="configurationFeatures.length > 0">
            <template #header>
                <div class="panel-header p-d-flex p-flex-wrap  p-ai-center">
                    <h5 class="p-mr-2">Configurations</h5>
                    <div class="p-mr-2">
                        <SplitButton
                            v-if="softSystemLoaded"
                            class="p-button-success" label="Add" icon="pi pi-plus" @click="$emit('get-config-example')">
                        </SplitButton>
                    </div>
<!--                    <div class="p-mr-2"><Button class="p-button-danger" label="Delete" icon="pi pi-times"/></div>-->
                    <div class="p-mr-2"><Button label="Export" icon="pi pi-external-link" class="p-button-outlined"/></div>
                </div>
            </template>
            <DataTable class="editable-cells-table p-mb-2" :rowHover="true" :autoLayout="true" :value="configurations"
                       editMode="cell" @cell-edit-complete="onCellEditComplete">
                <Column v-for="feature of configurationFeatures" :field="feature.name" :header="feature.name"
                        :key="feature.name" bodyStyle="">
                    <template v-if="feature.name === 'name'" #editor="slotProps">
                        <div class="p-grid" @mouseover="hover=true" @mouseleave="hover=false">
                            <InputText v-model="slotProps.data[feature.name]"
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
            <div class="p-d-flex p-jc-center">
                <Button v-if="configurations.length < 1" class="p-button p-mr-2" label="Submit" disabled="disabled"/>
                <Button v-else class="p-button p-mr-2" label="Submit"  @click="$emit('submit-configs')"/>
            </div>
        </Panel>
        <Panel v-else>
            <template class="p-p-0" #header>
                <h5 class="p-m-0">Empty</h5>
            </template>
            Please select a software system.
        </Panel>
    </div>
</template>

<script>
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import InputText from 'primevue/inputtext';
import Panel from 'primevue/panel';
import Button from 'primevue/button';
import SplitButton from 'primevue/splitbutton';




export default {
    name: "ConfigTable",
    components: {
        DataTable,
        Column,
        InputText,
        Panel,
        Button,
        SplitButton
    },
    props: [
        "configurationFeatures",
        "configurations",
        "softSystemLoaded"
    ],

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
.panel-header {
    margin-top: -0.5rem;
    margin-bottom: -0.5rem;
}
</style>

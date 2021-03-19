<template>
    <div class="mb-3">
        <Panel v-if="softSystemLoaded">
            <template #header>
                <div class="panel-header d-flex justify-content-between flex-wrap" style="width: 100%">
                    <div class="d-flex align-items-center flex-wrap">
                        <b-button class="mr-1" variant="link" @click="collapse"><font-awesome-icon :icon="visible ? 'chevron-up' : 'chevron-down'" class="text-secondary"/></b-button>
                        <h5 class="p-mb-0 p-mr-2">Configurations</h5>
                        <div class="p-mr-2">
                            <b-dropdown v-if="softSystemLoaded"
                                        right split variant="success"
                                        @click="$emit('get-config-example')">
                                <template #button-content>
                                    <font-awesome-icon class="mr-1" icon="plus" fixed-width/>Add
                                </template>
                                <ImportDropdownItem :systemName="systemName"
                                                    :binaryFeatures="systemFeatures.binaryFeatures"
                                                    :numericFeatures="Object.keys(systemFeatures.numericFeatures)"
                                                    :fileCheck="true" @load-data="loadData"/>
                                <b-dropdown-item-button @click="$emit('click-optimize')">
                                    <font-awesome-icon icon="compass" class="mr-1 text-secondary" fixed-width/>Optimize
                                </b-dropdown-item-button>
                                <b-dropdown-item-button @click="$emit('click-near-optimum', '', '', true)">
                                    <font-awesome-icon icon="chart-line" class="mr-1 text-secondary" fixed-width/>Near-Optimum
                                </b-dropdown-item-button>
                            </b-dropdown>
                        </div>
                        <div class="p-mr-2">
                            <ExportButton
                                label="Export" :systemName="systemName" :data="exportData"
                                @click="generateExportData">
                            </ExportButton>
                        </div>
                    </div>
                    <div class="p-ml-auto p-mt-2"><SelectButton v-model="selectedViewOption" :options="options"/></div>
                </div>
            </template>
            <b-collapse id="collapse-configs" v-model="visible">
                <div :class= "selectedViewOption === 'simple' ? 'panel-content d-flex' : 'd-none'"
                     style="overflow-x: auto;">
                    <div :class="selectedViewOption === 'simple' ? 'd-flex' : 'd-none'">
                        <template v-for="(config, index) in configurations">
                            <ConfigCard
                                :config="config"
                                :system-features="systemFeatures"
                                editable
                                @duplicate-config="$emit('duplicate-config', index)"
                                @del-config="$emit('del-config', index)"
                                @click-optimize="$emit('click-optimize', config.name, '')"
                                @update-config-name="$emit('update-config-name', index, $event)"
                                @add-feature="$emit('update-feature', index, $event, true)"
                                @remove-feature="$emit('update-feature', index, $event, false)"
                            />
                        </template>
                    </div>
                </div>
                <DataTable v-show="selectedViewOption === 'extended'"
                           class="p-datatable editable-cells-table panel-content"
                           :rowHover="true"
                           :autoLayout="true"
                           :value="configurations"
                           editMode="cell">
                    <Column field="name"
                            header="name"
                            key="name">
                        <template #body="slotProps">
                            {{slotProps.data['name']}}
                        </template>
                        <template #editor="slotProps">
                            <div class="align-items-center">
                                <InputText v-model="slotProps.data['name']"
                                           class="p-inputtext-sm mr-1 my-1" />
                                <div>
                                    <b-button class="m-0" variant="info" size="sm"
                                              @click="$emit('duplicate-config', slotProps.index)">
                                        <font-awesome-icon icon="copy" class="" fixed-width/>
                                    </b-button>
                                    <b-button class="m-0" variant="danger" size="sm"
                                              @click="$emit('del-config', slotProps.index)">
                                        <font-awesome-icon icon="times" class="" fixed-width/>
                                    </b-button>
                                    <b-button class="m-0" variant="success" size="sm"
                                              @click="$emit('click-optimize', slotProps.data['name'])">
                                        <font-awesome-icon icon="compass" class="" fixed-width/>
                                    </b-button>
                                </div>
                            </div>
                        </template>
                    </Column>
                    <Column v-for="feature of systemFeatures.binaryFeatures"
                            :field="feature"
                            :header="feature"
                            :key="feature">
                        <template #body="slotProps">
                            <label class="m-0">
                                <input type="checkbox" :checked="slotProps.data[feature]"
                                       @change="$emit('update-feature', slotProps.index, feature, !slotProps.data[feature])"/>
                            </label>
                        </template>
                    </Column>
                    <Column v-for="feature of Object.keys(systemFeatures.numericFeatures)"
                            :field="feature"
                            :header="feature"
                            :key="feature">
                        <template #body="slotProps">
                            <label>
                                <CustomSpinButton
                                    :value="slotProps.data[feature]"
                                    :value.sync="slotProps.data[feature]"
                                    :step-function="systemFeatures.numericFeatures[feature].stepFunction"
                                    :min="systemFeatures.numericFeatures[feature].min"
                                    :max="systemFeatures.numericFeatures[feature].max"
                                />
                            </label>
                        </template>
                    </Column>
                </DataTable>
                <div class="panel-footer d-flex justify-content-center">
                    <b-button :disabled="configurations.length < 1" variant="primary"
                              @click="$emit('submit-configs')">
                        Submit
                    </b-button>
                </div>
            </b-collapse>
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
import SelectButton from 'primevue/selectbutton';
import ScrollPanel from 'primevue/scrollpanel';
import OverlayPanel from 'primevue/overlaypanel';

import ConfigCard from "./ConfigCard";
import Chip from './Chip';
import ExportButton from "./ExportButton";
import ImportDropdownItem from "./ImportDropdownItem";
import CustomSpinButton  from "./SpinButton";

import configManagementMixin from "../mixins/configManagementMixin";

import { mapFields, mapMultiRowFields } from "vuex-map-fields";

export default {
    name: "ConfigArea",
    mixins: [configManagementMixin],
    components: {
        DataTable,
        Column,
        InputText,
        Panel,
        SelectButton,
        ScrollPanel,
        OverlayPanel,
        ConfigCard,
        Chip,
        ExportButton,
        ImportDropdownItem,
        CustomSpinButton
    },
    props: [
        "systemName",
        "softSystemLoaded"
    ],

    data() {
        return {
            selectedViewOption: 'simple',
            options: ['simple', 'extended'],
            renamedConfigString: '',
            visible: true,
            totalRows: 1,
            currentPage: 1,
            exportData: [],
        }
    },

    computed: {
        ...mapFields(
            'configurationStore',
            {
                systemFeatures: 'systemFeatures',
                configs: 'configurations'
            }
        ),
        ...mapMultiRowFields(
            'configurationStore',
            [
                "configurations"
            ]
        ),
    },

    methods: {
        collapse() {
            this.visible = !this.visible;
        },
        loadData(data) {
            this.$emit('load-data', data);
        },
        generateExportData() {
            let exportData = [...this.configs];
            exportData.forEach(function(obj) { delete obj.properties; delete obj.dissectedProperties });

            this.exportData = exportData;
        },
        makeToast(variant = null) {
            this.$bvToast.toast('Function is not implemented yet.', {
                title: 'Unsupported function',
                variant: variant,
                solid: true
            })
        }

    },
}
</script>

<style scoped>

    .panel-header {
        margin-top: -0.5rem;
        margin-bottom: -0.5rem;
    }

    .panel-content {
        margin: -1rem -1rem 0 -1rem;
        padding-bottom: 0.5em;
    }

    .panel-footer {
        margin: 0 -0.5rem -0.5rem -0.5rem;
    }

</style>

<style>

    .dropdown:active .dropdown:focus .dropdown-menu .dropdown-item {
        outline: none;
        box-shadow: none;
    }
    .hidden-header {
        display: none;
    }

    .table tr, .table td {
        padding: 0.25rem 1.5rem !important;
    }

</style>

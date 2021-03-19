<template>
    <div class="config-area mb-3">
        <b-card v-if="softSystemLoaded" header-class="p-2" no-body>
            <template #header>
                <div class="d-flex justify-content-between flex-wrap" style="width: 100%">
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
                <div :class= "selectedViewOption === 'simple' ? 'config-area-content d-flex mb-1' : 'd-none'"
                     style="overflow-x: auto;"
                >
                    <div class="config-row d-flex">
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
                <b-table
                    v-show="selectedViewOption === 'extended'" :items="configs" :fields="configTableFields"
                    class="mb-1" thead-class="header-light"
                    style="white-space: nowrap" responsive hover
                >
                    <template v-for="header in configTableFields" #[`head(${header})`]="data">
                        <h6 class="mb-0">{{ header }}</h6>
                    </template>
                    <template v-for="field in configTableFields" #[`cell(${field})`]="cellData">
                        <template v-if="field === 'name'">
                            <EditCell>
                                {{ cellData.value }}
                                <template #edit>
                                    <b-form-input class="mb-1" style="min-width: 12rem" v-model="cellData.item.name" maxlength="24"/>
                                    <div>
                                        <b-button class="m-0" variant="info" size="sm"
                                                  @click="$emit('duplicate-config', cellData.index)">
                                            <font-awesome-icon icon="copy" class="" fixed-width/>
                                        </b-button>
                                        <b-button class="m-0" variant="success" size="sm"
                                                  @click="$emit('click-optimize', cellData.value)">
                                            <font-awesome-icon icon="compass" class="" fixed-width/>
                                        </b-button>
                                        <b-button class="m-0" variant="danger" size="sm"
                                                  @click="$emit('del-config', cellData.index)">
                                            <font-awesome-icon icon="times" class="" fixed-width/>
                                        </b-button>
                                    </div>
                                </template>
                            </EditCell>
                        </template>
                        <div v-else-if="typeof cellData.item[field] === 'boolean'">
                            <b-form-checkbox v-model="cellData.item[field]"></b-form-checkbox>
                        </div>
                        <CustomSpinButton
                            v-else-if="typeof cellData.item[field] === 'number'"
                            :value="cellData.item[field]"
                            :value.sync="cellData.item[field]"
                            :step-function="systemFeatures.numericFeatures[field].stepFunction"
                            :min="systemFeatures.numericFeatures[field].min"
                            :max="systemFeatures.numericFeatures[field].max"
                        />
                    </template>
                </b-table>
                <div class="config-area-footer mb-1 d-flex justify-content-center">
                    <b-button :disabled="configurations.length < 1" variant="primary"
                              @click="$emit('submit-configs')">
                        Submit
                    </b-button>
                </div>
            </b-collapse>
        </b-card>
        <b-card v-else>
            <template class="p-p-0" #header>
                <h5 class="p-m-0">Empty</h5>
            </template>
            Please select a software system.
        </b-card>
    </div>
</template>

<script>
import SelectButton from 'primevue/selectbutton';

import ConfigCard from "./ConfigCard";
import Chip from './Chip';
import ExportButton from "./ExportButton";
import ImportDropdownItem from "./ImportDropdownItem";
import CustomSpinButton  from "./SpinButton";
import EditCell from "./EditCell";

import { mapFields, mapMultiRowFields } from "vuex-map-fields";

export default {
    name: "ConfigArea",
    components: {
        SelectButton,
        ConfigCard,
        Chip,
        ExportButton,
        ImportDropdownItem,
        CustomSpinButton,
        EditCell
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

        configTableFields() {
            return ['name'].concat(this.systemFeatures.binaryFeatures, Object.keys(this.systemFeatures.numericFeatures));
        }
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
    },
}
</script>

<style scoped>
</style>

<style>
    .dropdown:active .dropdown:focus .dropdown-menu .dropdown-item {
        outline: none;
        box-shadow: none;
    }
    .hidden-header {
        display: none;
    }

    .header-light {
        background-color: #f8f9fa !important;
    }

    .table td {
        vertical-align: middle !important;
    }

    .config-area th {
        border-bottom: 0 !important;
        border-top: 0 !important;
        padding: 0.875rem !important;
    }

</style>

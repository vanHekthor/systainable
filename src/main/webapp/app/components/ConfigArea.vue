<template>
    <div class="config-area mb-3">
        <b-card v-if="softSystemLoaded" header-class="p-2" no-body>
            <template #header>
                <div class="d-flex justify-content-between flex-wrap" style="width: 100%">
                    <div class="d-flex align-items-center flex-wrap">
                        <b-button class="mr-0 mr-sm-1" variant="link" @click="collapse">
                            <font-awesome-icon :icon="visible ? 'chevron-up' : 'chevron-down'" class="text-secondary"/>
                        </b-button>
                        <h5 class="mb-0 mr-2">
                            <span class="d-none d-sm-block">Configurations</span>
                            <span class="d-block d-sm-none">Configs</span>
                        </h5>
                        <div class="mr-2">
                            <b-dropdown v-if="softSystemLoaded"
                                        right split variant="success"
                                        @click="$emit('get-config-example')">
                                <template #button-content>
                                    <font-awesome-icon class="mr-1" icon="plus" fixed-width/>Add
                                </template>
                                <b-dropdown-item-button @click="triggerExport" class="d-block d-sm-none">
                                    <font-awesome-icon icon="file-export" class="mr-1 text-secondary" fixed-width/>Export
                                </b-dropdown-item-button>
                                <ImportDropdownItem
                                    :systemName="systemName"
                                    :binaryFeatures="systemFeatures.binaryFeatures"
                                    :numericFeatures="Object.keys(systemFeatures.numericFeatures)"
                                    :fileCheck="true" @load-data="loadData"
                                />
                                <b-dropdown-divider></b-dropdown-divider>
                                <b-dropdown-item-button @click="triggerExport">
                                    <font-awesome-icon icon="trash" class="mr-1 text-secondary" fixed-width/>Delete all
                                </b-dropdown-item-button>
                                <b-dropdown-divider></b-dropdown-divider>
                                <b-dropdown-item-button @click="$emit('click-optimize')">
                                    <font-awesome-icon icon="compass" class="mr-1 text-secondary" fixed-width/>Optimize
                                </b-dropdown-item-button>
                                <b-dropdown-item-button @click="$emit('click-near-optimum', '', '', true)">
                                    <font-awesome-icon icon="chart-line" class="mr-1 text-secondary" fixed-width/>Near-Optimum
                                </b-dropdown-item-button>
                            </b-dropdown>
                        </div>
                        <div class="mr-2">
                            <ExportButton
                                class="d-none d-sm-block"
                                ref="export"
                                :systemName="systemName" :data="exportData"
                                @click="generateExportData"
                            >
                                Export
                            </ExportButton>
                        </div>
                    </div>
                    <div class="d-flex align-items-center align-content-end">
                        <b-form-group class="d-none d-sm-block m-1" v-slot="{ ariaDescribedby }">
                            <b-form-radio-group
                                v-model="selectedViewOption"
                                :options="options"
                                :aria-describedby="ariaDescribedby"
                                button-variant="outline-primary"
                                buttons
                            ></b-form-radio-group>
                        </b-form-group>
                        <b-form-checkbox class="d-block d-sm-none ml-2 my-1" @change="toggleExtendedView" :checked="selectedViewOption === 'extended'" name="check-button" switch>
                            <font-awesome-icon icon="table" class="text-secondary" fixed-width/>
                        </b-form-checkbox>
                    </div>
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
        triggerExport() {
            // shady way to trigger event of nested component i.e. the b-button click in ExportButton.vue
            this.$refs.export.$refs.button.click();
        },
        toggleExtendedView(isVisible) {
            if (isVisible) {
                this.selectedViewOption = 'extended';
            } else {
                this.selectedViewOption = 'simple';
            }
        }
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

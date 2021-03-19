<template>
    <div class="mb-3">
        <b-card v-if="softSystemLoaded" header-class="p-2" no-body>
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
                <b-table style="white-space: nowrap" :items="configs" :fields="configTableFields" head-variant="light" responsive hover>
                    <template v-for="header in configTableFields" #[`head(${header})`]="data">
                        {{ header }}
                    </template>
                    <template v-for="field in configTableFields" #[`cell(${field})`]="cellData">
                        <template v-if="field === 'name'">
                            <div class="p-2" @click.stop="onClick">
                                <div v-if="!editVisible">
                                    {{ cellData.value }}
                                </div>
                                <div v-else v-click-outside="onClickOutside">
                                    <b-form-input class="mb-1" style="min-width: 12rem" v-model="cellData.item.name"/>
                                    <div>
                                        <b-button class="m-0" variant="info" size="sm"
                                                  @click="$emit('duplicate-config', cellData.index)">
                                            <font-awesome-icon icon="copy" class="" fixed-width/>
                                        </b-button>
                                        <b-button class="m-0" variant="danger" size="sm"
                                                  @click="$emit('del-config', cellData.index)">
                                            <font-awesome-icon icon="times" class="" fixed-width/>
                                        </b-button>
                                        <b-button class="m-0" variant="success" size="sm"
                                                  @click="$emit('click-optimize', cellData.value)">
                                            <font-awesome-icon icon="compass" class="" fixed-width/>
                                        </b-button>
                                    </div>
                                </div>
                            </div>
                        </template>
                        <div v-else-if="typeof cellData.item[field] === 'boolean'"><b-form-checkbox v-model="cellData.item[field]"></b-form-checkbox></div>
                        <CustomSpinButton
                            v-else-if="typeof cellData.item[field] === 'number'"
                            :value="cellData.item[field]"
                            :value.sync="cellData.item[field]"
                            :step-function="systemFeatures.numericFeatures[field].stepFunction"
                            :min="systemFeatures.numericFeatures[field].min"
                            :max="systemFeatures.numericFeatures[field].max"
                        />
                    </template>

<!--                    <template v-for="field in editableFields" v-slot:[`cell(${field.key})`]=" data ">
                        <template>{{ data }}</template>
                    </template>
                    <template v-slot:cell(actions)="{ item }">
                        <b-button-group v-if="userRow && userRow.id === item.id">
                            <b-button variant="success" @click="saveEdit">
                                Save
                            </b-button>
                            <b-button variant="danger" @click="resetEdit">
                                Cancel
                            </b-button>
                        </b-button-group>
                        <b-button v-else variant="primary" @click="editUser(item)">
                            Edit
                        </b-button>
                    </template>-->
                </b-table>
                <div class="panel-footer d-flex justify-content-center">
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

            editVisible: false,
            userRow: null,
            fields: [
                { key: "name" },
                { key: "first_name", editable: true },
                { key: "last_name", editable: true },
                { key: "age", editable: true, type: "number", isNumber: true },
                { key: "actions" }
            ],
            items: [
                { id: 1, first_name: "Mikkel", last_name: "Hansen", age: 56 },
                { id: 2, first_name: "Mads", last_name: "Mikkelsen", age: 39 },
                { id: 3, first_name: "Anders", last_name: "Matthesen", age: 42 }
            ]
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

        configTableItems() {
            let items = [...this.configs];
            items.forEach(function(obj) { delete obj.properties; delete obj.dissectedProperties });
            return items;
        },

        configTableFields() {
            return ['name'].concat(this.systemFeatures.binaryFeatures, Object.keys(this.systemFeatures.numericFeatures));
        }
    },

    methods: {
        onClick() {
            console.log('CLick!');
            this.editVisible = true;
        },
        onClickOutside() {
            console.log('CLick outside!');
            this.editVisible = false;
        },
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
        },
        editUser(user) {
            let doEdit = true;
            if (
                this.userRow &&
                !confirm(
                    "You have unsaved changes, are you sure you want to continue?"
                )
            ) {
                doEdit = false;
            }

            if (doEdit) {
                this.userRow = { ...user };
            }
        },
        saveEdit() {
            let user = this.items.find((u) => u.id === this.userRow.id);
            Object.assign(user, this.userRow);

            this.resetEdit();
        },
        resetEdit() {
            this.userRow = null;
        }

    },
}
</script>

<style scoped>

    .panel-header {
    }

    .panel-content {
        padding-bottom: 0.5em;
    }

    .panel-footer {
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

    .table td {
        vertical-align: middle !important;
    }

    .config-card-header tr, .config-card-header td {
        padding: 0.25rem 1.5rem !important;
    }

</style>

<template>
    <div class="mb-3">
        <Panel v-if="softSystemLoaded">
            <template #header>
                <div class="panel-header p-d-flex p-jc-between p-flex-wrap" style="width: 100%">
                    <div class="p-d-flex p-ai-center p-flex-wrap">
                        <Button class="p-button p-button-text p-button-sm p-button-plain" :icon="collapseButtonIcon" aria-controls="collapse-configs"
                                @click="collapse"/>
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
                    <div class="p-ml-auto"><SelectButton v-model="selectedViewOption" :options="options"/></div>
                </div>
            </template>
            <b-collapse id="collapse-configs" v-model="visible">
                <div v-if="selectedViewOption==='simple'"
                     class="panel-content p-d-flex"
                     style="overflow-x: auto;">
                    <div class="p-d-flex">
                        <div v-for="(config, index) in configurations" :key="index"
                             class="config-card p-shadow-1"
                             style="width: 18rem">
                            <div class="config-card-header p-p-1">
                                <div class="p-d-flex p-ai-center p-jc-between">
                                    <h6 class="p-my-0 py-1 p-ml-3" style="max-width: 12rem; overflow-x: auto">
                                        {{config.name}}
                                    </h6>
                                    <div>
                                        <b-button class="p-1 no-outline" variant="link"
                                                  @click.stop="toggle($event, index)">
                                            <font-awesome-icon icon="plus" :style="{ color: '#6c757d' }" fixed-width/>
                                        </b-button>
                                        <b-dropdown class="no-outline" toggle-class="p-1 no-outline" variant="link" boundary="viewport" no-caret
                                                    @toggle="untoggle()">
                                            <template #button-content>
                                                <font-awesome-icon icon="cog" :style="{ color: '#6c757d' }" fixed-width/>
                                            </template>
                                            <b-dropdown-item-button @click="$emit('duplicate-config',index)">
                                                <font-awesome-icon icon="copy" class="mr-1" :style="{ color: '#6c757d' }" fixed-width/>
                                                duplicate
                                            </b-dropdown-item-button>
                                            <b-dropdown-item-button v-b-modal="'modal-' + index" @click="renamedConfigString=config.name">
                                                <font-awesome-icon icon="edit" class="mr-1" :style="{ color: '#6c757d' }" fixed-width/>
                                                rename
                                            </b-dropdown-item-button>
                                            <b-dropdown-item-button @click="$emit('del-config', index)">
                                                <font-awesome-icon icon="trash" class="mr-1" :style="{ color: '#6c757d' }" fixed-width/>
                                                delete
                                            </b-dropdown-item-button>
                                            <b-dropdown-divider></b-dropdown-divider>
                                            <b-dropdown-item-button @click="$emit('click-optimize', config.name, '')">
                                                <font-awesome-icon icon="compass" class="mr-1" :style="{ color: '#6c757d' }" fixed-width/>
                                                optimize
                                            </b-dropdown-item-button>
                                        </b-dropdown>
                                        <b-modal :id="'modal-' + index" centered :title="'Rename configuration: ' + config.name"
                                                 @ok="$emit('update-config-name', index, renamedConfigString)">
                                            <b-form-input type="text" :value="config.name" v-model="renamedConfigString" maxlength="24"></b-form-input>
                                        </b-modal>
                                    </div>
                                </div>
                            </div>
                            <div class="config-card-content p-p-1"
                                 style="min-height: 6rem; max-height: 16rem; overflow-y: auto">
                                <div class="badges p-d-flex p-flex-wrap p-jc-center">
                                    <template v-for="(featureName) in systemFeatures.binaryFeatures">
                                        <Chip v-if="featureName !== 'name' && config[featureName]"
                                              class="binary-chip p-m-1" :label="featureName" removable
                                              @remove="$emit('update-feature', index, featureName, false)"/>
                                    </template>
                                    <template v-for="(featureName) in Object.keys(systemFeatures.numericFeatures)">
                                        <Chip :id="'numeric-chip' + index + featureName"
                                              class="numeric-chip p-m-1" :label="featureName + ': ' + config[featureName]"/>
                                        <b-popover
                                            :target="'numeric-chip' + index + featureName"
                                            :title="featureName"
                                            triggers="hover"
                                            boundary="viewport"
                                            placement="bottom">
                                            <CustomSpinButton
                                                :value="config[featureName]"
                                                :value.sync="config[featureName]"
                                                :step-function="systemFeatures.numericFeatures[featureName].stepFunction"
                                                :min="systemFeatures.numericFeatures[featureName].min"
                                                :max="systemFeatures.numericFeatures[featureName].max"
                                            />
                                        </b-popover>
                                    </template>
                                </div>
                            </div>
                            <div class="config-card-footer p-d-flex p-jc-center">
                            </div>
                        </div>
                    </div>
                </div>
                <DataTable v-if="selectedViewOption==='extended'"
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
        <OverlayPanel class="no-shadow" ref="op" v-click-outside="untoggle">
            <b-input-group size="sm">
                <b-form-input
                    id="filter-input"
                    v-model="featureFilter"
                    type="search"
                    placeholder="Type to Search"
                    class="mx-2 mb-2"
                ></b-form-input>
            </b-input-group>
            <b-table class="m-0"
                     borderless
                     hover :items="unselectedFeatures"
                     thead-class="hidden-header"
                     :filter="featureFilter"
                     :filter-included-fields="featureFilterOn"
                     @filtered="onFiltered"
                     @row-clicked="addFeatureToConfig"
            ></b-table>
        </OverlayPanel>
    </div>
</template>

<script>
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import InputText from 'primevue/inputtext';
import Panel from 'primevue/panel';
import Button from 'primevue/button';
import SelectButton from 'primevue/selectbutton';
import Card from 'primevue/card';
import ScrollPanel from 'primevue/scrollpanel';
import OverlayPanel from 'primevue/overlaypanel';
import Chip from './Chip';
import ExportButton from "./ExportButton";
import ImportDropdownItem from "./ImportDropdownItem";
import CustomSpinButton  from "./SpinButton";

import { mapFields, mapMultiRowFields } from "vuex-map-fields";

export default {
    name: "ConfigArea",
    components: {
        DataTable,
        Column,
        InputText,
        Panel,
        Button,
        SelectButton,
        Card,
        ScrollPanel,
        OverlayPanel,
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
            collapseButtonIcon: 'pi pi-chevron-up',
            visible: true,
            configIndex: 0,
            unselectedFeatures: [],
            totalRows: 1,
            currentPage: 1,
            featureFilter: null,
            featureFilterOn: [],
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
            if (this.visible) {
                this.collapseButtonIcon = 'pi pi-chevron-up';
            } else {
                this.collapseButtonIcon = 'pi pi-chevron-down';
            }
            console.log(this.configurations);
        },
        toggle(event, index) {
            this.configIndex = index;
            this.unselectedFeatures = [];
            for (let featureName of this.systemFeatures.binaryFeatures) {
                if (!this.configurations[index][featureName]) {
                    this.unselectedFeatures.push({feature: featureName});
                }
            }
            this.$refs.op.toggle(event);
        },
        untoggle() {
            this.featureFilter=null;
            this.$refs.op.hide();
        },
        onFiltered(filteredItems) {
            // Trigger pagination to update the number of buttons/pages due to filtering
            this.totalRows = filteredItems.length
            this.currentPage = 1
        },
        addFeatureToConfig(item) {
            this.$emit('update-feature', this.configIndex, item.feature, true);
            this.untoggle();
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

.config-card-header{
    background-color: #f8f9fa;
    border-bottom: 1px solid #e3e3e3;
}

.binary-chip {
    background: #2196F3;
    color: #fff;
}

.numeric-chip {
    background: #0064C1;
    color: #fff;
}

.no-outline:focus,.no-outline:active {
    outline: none !important;
    box-shadow: none !important;
}
</style>
<style>
.dropdown:active .dropdown:focus .dropdown-menu .dropdown-item {
    outline: none;
    box-shadow: none;
}

.p-overlaypanel {
    box-shadow: none !important;
    border: 1px rgba(0, 0, 0, 0.15) !important;
    border-style: solid !important;
    border-radius: 1px;
    --overlayArrowLeft: none !important;
    margin: 0.125rem;
}

.p-overlaypanel-content {
    padding: 0.5rem 0 !important;
}

.table tr, .table td {
    padding: 0.25rem 1.5rem !important;
}

.hidden-header {
    display: none;
}
</style>

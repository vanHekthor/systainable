<template>
    <div class="p-px-3">
        <Panel v-if="configurationFeatures.length > 0">
            <template #header>
                <div class="panel-header p-d-flex p-jc-between p-flex-wrap" style="width: 100%">
                    <div class="p-d-flex p-ai-center p-flex-wrap">
                        <Button class="p-button p-button-text p-button-sm p-button-plain" :icon="collapseButtonIcon" aria-controls="collapse-configs"
                                @click="collapse"/>
                        <h5 class="p-mb-0 p-mr-2">Configurations</h5>
                        <div class="p-mr-2">
                            <SplitButton
                                v-if="softSystemLoaded"
                                class="p-button-success" label="Add" icon="pi pi-plus" :model="items"
                                @click="$emit('get-config-example')">
                            </SplitButton>
                        </div>
                        <div class="p-mr-2">
                            <Button class="p-button-outlined" label="Export" icon="pi pi-external-link"
                                    @click="makeToast('info')"/>
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
                                    <h6 class="p-my-0 p-ml-3">{{config.name}}</h6>
                                    <div>
                                        <b-button class="p-1 no-outline" variant="link"
                                                  @click.stop="toggle($event, index)">
                                            <font-awesome-icon icon="plus" :style="{ color: '#6c757d' }" fixed-width/>
                                        </b-button>
                                        <b-dropdown class="no-outline" toggle-class="p-1 no-outline" variant="link" no-caret
                                                    @toggle="untoggle()">
                                            <template #button-content>
                                                <font-awesome-icon icon="cog" :style="{ color: '#6c757d' }" fixed-width/>
                                            </template>
                                            <b-dropdown-item-button v-b-modal="'modal-' + config.name" @click="renamedConfigString=config.name">
                                                <font-awesome-icon icon="edit" class="mr-1" :style="{ color: '#6c757d' }" fixed-width/>
                                                rename
                                            </b-dropdown-item-button>
                                            <b-dropdown-item-button @click="$emit('del-config', index)">
                                                <font-awesome-icon icon="trash" class="mr-1" :style="{ color: '#6c757d' }" fixed-width/>
                                                delete
                                            </b-dropdown-item-button>
                                        </b-dropdown>
                                        <b-modal :id="'modal-' + config.name" centered :title="'Rename configuration: ' + config.name"
                                                 @ok="$emit('update-config-name', index, renamedConfigString)">
                                            <b-form-input type="text" :value="config.name" v-model="renamedConfigString" maxlength="18"></b-form-input>
                                        </b-modal>
                                    </div>
                                </div>
                            </div>
                            <div class="config-card-content p-p-1"
                                 style="max-height: 16rem; overflow-y: auto">
                                <div class="badges p-d-flex p-flex-wrap p-jc-center">
                                    <template v-for="(value, featureName) in config">
                                        <Chip v-if="featureName !== 'name' && value"
                                              class="custom-chip p-m-1" :label="featureName" removable
                                              @remove="$emit('update-feature', index, featureName)"/>
                                    </template>
                                </div>
                            </div>
                            <div class="config-card-footer p-d-flex p-jc-center">
                            </div>
                        </div>
                    </div>
                </div>
                <DataTable v-if="selectedViewOption==='extended'"
                           class="editable-cells-table panel-content"
                           :rowHover="true"
                           :autoLayout="true"
                           :value="configurations"
                           editMode="cell">
                    <Column v-for="feature of configurationFeatures"
                            :field="feature.name"
                            :header="feature.name"
                            :key="feature.name">
                        <template v-if="feature.name === 'name'" #editor="slotProps">
                            <div class="p-grid">
                                <InputText v-model="slotProps.data[feature.name]"
                                           class="p-inputtext-sm" />
                                <Button class="p-button-danger p-button-sm p-mt-2" label="Delete" icon="pi pi-times"
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
                <div class="panel-footer p-d-flex p-jc-center">
                    <Button v-if="configurations.length < 1"
                            class="p-button" label="Submit" disabled="disabled"/>
                    <Button v-else
                            class="p-button" label="Submit"
                            @click="$emit('submit-configs')"/>
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
import SplitButton from 'primevue/splitbutton';
import SelectButton from 'primevue/selectbutton';
import Card from 'primevue/card';
import ScrollPanel from 'primevue/scrollpanel';
import OverlayPanel from 'primevue/overlaypanel';
import Chip from './Chip';


export default {
    name: "ConfigTable",
    components: {
        DataTable,
        Column,
        InputText,
        Panel,
        Button,
        SplitButton,
        SelectButton,
        Card,
        ScrollPanel,
        OverlayPanel,
        Chip
    },
    props: [
        "configurationFeatures",
        "configurations",
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
            items: [
                {
                    label: 'Load',
                    icon: 'pi pi-upload',
                    command: () => {
                        this.makeToast('info');
                    }
                },
                {
                    label: 'Optimize',
                    icon: 'pi pi-compass',
                    command: () => {
                        this.makeToast('info');
                    }
                }
            ],
        }
    },

    created() {
    },

    methods: {
        collapse() {
            this.visible = !this.visible;
            if (this.visible) {
                this.collapseButtonIcon = 'pi pi-chevron-up';
            } else {
                this.collapseButtonIcon = 'pi pi-chevron-down';
            }
        },
        toggle(event, index) {
            this.configIndex = index;
            this.unselectedFeatures = [];
            for (let featureName of Object.keys(this.configurations[index])) {
                if (featureName==="name")
                    continue;
                if (!this.configurations[index][featureName]) {
                    this.unselectedFeatures.push({feature: featureName});
                }
            }
            this.$refs.op.toggle(event);
        },
        untoggle() {
            this.featureFilter=null;
            this.$refs.op.hide();
            console.log('Overlay was hid');
        },
        onFiltered(filteredItems) {
            // Trigger pagination to update the number of buttons/pages due to filtering
            this.totalRows = filteredItems.length
            this.currentPage = 1
        },
        addFeatureToConfig(item) {
            this.$emit('update-feature', this.configIndex, item.feature);
            this.untoggle();
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
        background-color: #f5f5f5;
        border-bottom: 1px solid #e3e3e3;
    }

    .custom-chip {
        background: #2196F3;
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

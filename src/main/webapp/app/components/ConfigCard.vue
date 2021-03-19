<template>
    <div :class="editable ? 'config-area-card mr-0' : 'dialog-config-card shadow-sm mr-0'"
         style="width: 18rem;">
        <div class="config-card-header p-1">
            <div class="d-flex align-items-center justify-content-between">
                <h6 class="p-my-0 py-1 p-ml-3" style="max-width: 12rem; overflow-x: auto">
                    {{config.name}}
                </h6>
                <div v-if="editable">
                    <b-dropdown
                        class="no-outline" toggle-class="p-1 no-outline" ref="dropdown-feature-list"
                        variant="link" boundary="window" no-caret :disabled="unselectedFeatures.length === 0"
                    >
                        <template #button-content>
                            <font-awesome-icon icon="plus" :style="{ color: '#6c757d' }" fixed-width/>
                        </template>
                        <b-input-group size="sm">
                            <b-form-input
                                id="filter-input"
                                v-model="featureFilter"
                                type="search"
                                placeholder="Type to Search"
                                class="mx-2 mb-2"
                            ></b-form-input>
                        </b-input-group>
                        <b-table
                            class="m-0"
                            borderless
                            hover
                            :items="toggleFeatureList()"
                            thead-class="hidden-header"
                            :filter="featureFilter"
                            :filter-included-fields="featureFilterOn"
                            @filtered="onFiltered"
                            @row-clicked="addFeatureToConfig"
                        ></b-table>
                    </b-dropdown>
                    <b-dropdown class="no-outline" toggle-class="p-1 no-outline" variant="link" boundary="window" no-caret
                                >
                        <template #button-content>
                            <font-awesome-icon icon="cog" :style="{ color: '#6c757d' }" fixed-width/>
                        </template>
                        <b-dropdown-item-button @click="$emit('duplicate-config')">
                            <font-awesome-icon icon="copy" class="mr-1" :style="{ color: '#6c757d' }" fixed-width/>
                            duplicate
                        </b-dropdown-item-button>
                        <b-dropdown-item-button v-b-modal="'modal-' + config.name" @click="renamedConfigString=config.name">
                            <font-awesome-icon icon="edit" class="mr-1" :style="{ color: '#6c757d' }" fixed-width/>
                            rename
                        </b-dropdown-item-button>
                        <b-dropdown-item-button @click="$emit('del-config')">
                            <font-awesome-icon icon="trash" class="mr-1" :style="{ color: '#6c757d' }" fixed-width/>
                            delete
                        </b-dropdown-item-button>
                        <b-dropdown-divider></b-dropdown-divider>
                        <b-dropdown-item-button @click="$emit('click-optimize', config.name, '')">
                            <font-awesome-icon icon="compass" class="mr-1" :style="{ color: '#6c757d' }" fixed-width/>
                            optimize
                        </b-dropdown-item-button>
                    </b-dropdown>
                    <b-modal :id="'modal-' + config.name" centered :title="'Rename configuration: ' + config.name"
                             @ok="$emit('update-config-name', renamedConfigString)">
                        <b-form-input type="text" :value="config.name" v-model="renamedConfigString" maxlength="24"></b-form-input>
                    </b-modal>
                </div>
            </div>
        </div>
        <div class="config-card-content p-1"
             style="min-height: 6rem; max-height: 16rem; overflow-y: auto">
            <div class="badges d-flex flex-wrap justify-content-center">
                <div v-for="chip in chips">
                    <Chip v-if="chip.type === 'binary-default'"
                          class="binary-chip m-1" :label="chip.featureName" :removable="editable"
                          @remove="$emit('remove-feature', chip.featureName)"/>
                    <Chip v-if="chip.type === 'removed'"
                          class="removed-chip m-1" :label="chip.featureName"/>
                    <Chip v-if="chip.type === 'added'"
                          class="added-chip m-1" :label="chip.featureName"/>
                    <template v-if="chip.type === 'numeric-default'">
                        <Chip :id="`numeric-chip-${config.name}-${chip.featureName}`" class="numeric-chip m-1" :label="chip.featureName + ': ' + config[chip.featureName]"/>
                        <b-popover v-if="editable"
                            :target="`numeric-chip-${config.name}-${chip.featureName}`"
                            :title="chip.featureName"
                            triggers="hover"
                            boundary="viewport"
                            placement="bottom">
                            <CustomSpinButton
                                :value="config[chip.featureName]"
                                :value.sync="config[chip.featureName]"
                                :step-function="systemFeatures.numericFeatures[chip.featureName].stepFunction"
                                :min="systemFeatures.numericFeatures[chip.featureName].min"
                                :max="systemFeatures.numericFeatures[chip.featureName].max"
                            />
                        </b-popover>
                    </template>
                    <Chip v-if="chip.type === 'modified'"
                          class="modified-chip m-1" :label="chip.featureName + ': ' + config[chip.featureName]"/>
                </div>
            </div>
        </div>
        <div class="config-card-footer d-flex justify-content-center">
        </div>
<!--        <OverlayPanel class="no-shadow" ref="op" v-click-outside="hideFeatureList" :dismissable="true" @blur="hideFeatureList">
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
                     hover
                     :items="unselectedFeatures"
                     thead-class="hidden-header"
                     :filter="featureFilter"
                     :filter-included-fields="featureFilterOn"
                     @filtered="onFiltered"
                     @row-clicked="addFeatureToConfig"
            ></b-table>
        </OverlayPanel>-->
    </div>
</template>

<script>
import Chip from './Chip';
import CustomSpinButton  from "./SpinButton";


export default {
    name: "ConfigCard",

    components: {
        Chip,
        CustomSpinButton,
    },

    props: {
        config: {
            type: Object,
            required: true,
        },
        systemFeatures: {
            type: Object,
            required: true,
        },
        originalConfig: {
            type: Object,
            required: false,
            default: null,
        },
        editable: {
            type: Boolean,
            required: false,
            default: false,
        }
    },

    data() {
        return {
            renamedConfigString: '',
            featureFilter: null,
            featureFilterOn: [],
        }
    },

    computed: {
        /**
         * Computes the the array of chips that represent the features of a configuration. If there is an original configuration which the configuration that shall be
         * displayed originated from, special types of chips (removed/added/modified) are used depending on the changes that were made to the feature a chip represents.
         * @returns {Array} Array that contains chip objects
         */
        chips() {
            let chips = [];

            if (this.originalConfig != null) {
                for (let binaryFeatureName of this.systemFeatures.binaryFeatures) {
                    if (this.originalConfig[binaryFeatureName] === true && this.config[binaryFeatureName] === false) {
                        chips.push({featureName: binaryFeatureName, type: 'removed'});
                    } else if (this.originalConfig[binaryFeatureName] === false && this.config[binaryFeatureName] === true) {
                        chips.push({featureName: binaryFeatureName, type: 'added'});
                    } else if (this.config[binaryFeatureName]){
                        chips.push({featureName: binaryFeatureName, type: 'binary-default'});
                    }
                }

                for (let numericFeatureName of Object.keys(this.systemFeatures.numericFeatures)) {
                    if (this.originalConfig[numericFeatureName] !== this.config[numericFeatureName]) {
                        chips.push({featureName: numericFeatureName, type: 'modified'});
                    } else {
                        chips.push({featureName: numericFeatureName, type: 'numeric-default'});
                    }
                }

            } else {
                for (let binaryFeatureName of this.systemFeatures.binaryFeatures) {
                    if (this.config[binaryFeatureName]) {
                        chips.push({featureName: binaryFeatureName, type: 'binary-default'});
                    }
                }
                for (let numericFeatureName of Object.keys(this.systemFeatures.numericFeatures)) {
                    chips.push({featureName: numericFeatureName, type: 'numeric-default'});
                }
            }

            return chips;
        },

        unselectedFeatures() {
            let unselectedFeatures = [];
            for (let featureName of this.systemFeatures.binaryFeatures) {
                if (!this.config[featureName]) {
                    unselectedFeatures.push({ feature: featureName });
                }
            }

            return unselectedFeatures;
        }
    },

    methods: {
        toggleFeatureList() {
            let unselectedFeatures = [];
            for (let featureName of this.systemFeatures.binaryFeatures) {
                if (!this.config[featureName]) {
                    unselectedFeatures.push({ feature: featureName });
                }
            }

            return unselectedFeatures;
        },
        hideFeatureList() {
            this.featureFilter=null;
            this.$refs['dropdown-feature-list'].hide();
        },
        onFiltered(filteredItems) {
            // Trigger pagination to update the number of buttons/pages due to filtering
            this.totalRows = filteredItems.length
            this.currentPage = 1
        },
        addFeatureToConfig(item) {
            this.$emit('add-feature', item.feature);
            this.hideFeatureList();
        },
    }

}
</script>

<style scoped>
    .config-area-card {
        border-right: 1px solid #e3e3e3;
        border-bottom: 1px solid #e3e3e3;
    }

    .dialog-config-card {
        border: 1px solid #f5f5f5;
    }

    .config-card-header {
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

    .removed-chip {
        text-decoration: line-through;
        background: #ea0000;
        color: #fff;
    }

    .added-chip {
        background: #06db00;
        color: #fff;
    }

    .modified-chip {
        background: #7d00c1;
        color: #fff;
    }
    .no-outline:focus,.no-outline:active {
        outline: none !important;
        box-shadow: none !important;
    }
</style>

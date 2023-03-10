<template>
    <b-modal id="optiModal" :visible="displayOptimizationModal" @change="onChange" :title="globalOptimization ? 'Find Near-Optimal Configuration' : 'Optimization Search'" size="lg" centered
             @ok="$emit('ok')" @hide="$emit('hide')">
        <template #default>
            <b-container>
                <b-row v-if="!globalOptimization" class="mb-2 align-items-center">
                    <b-col cols="4">Configuration:</b-col>
                    <b-col>
                        <b-form-select :value="selectedUnoptimizedConfigName" @change="onChangeConfig" :options="configNames">
                            <template #first>
                                <b-form-select-option value="" disabled>-- Please select a configuration --</b-form-select-option>
                            </template>
                        </b-form-select>
                    </b-col>
                </b-row>
                <b-row class="mb-2 align-items-center">
                    <b-col cols="4">Property:</b-col>
                    <b-col>
                        <b-form-select :value="selectedOptimizationPropName" @change="onChangeProp" :options="propNames">
                            <template #first>
                                <b-form-select-option value="" disabled>-- Please select a property --</b-form-select-option>
                            </template>
                        </b-form-select>
                    </b-col>
                </b-row>
                <b-row v-if="!globalOptimization" class="mb-2 align-items-center">
                    <b-col cols="4">max. Difference:</b-col>
                    <b-col>
                        <b-form-spinbutton v-model="optimizationDistance" min="1" :max="maxOptimizationDistance" :disabled="spinButtonDisabled"></b-form-spinbutton>
                    </b-col>
                </b-row>
                <b-row v-if="!globalOptimization" class="mb-2 px-3 justify-content-center">
                    <b-button class="w-100" variant="primary" :disabled="searchDisabled"
                              @click="$emit('search-optimized-config', $event, selectedUnoptimizedConfigName, selectedOptimizationPropName, optimizationDistance)">
                        search
                    </b-button>
                </b-row>
                <b-row v-if="searchedForOptimizedConfig && !optimizedConfigFound">
                    <b-col><div class="text-danger">No better configuration found within max. difference range!</div></b-col>
                </b-row>
                <b-row v-if="searchedForOptimizedConfig && optimizedConfigFound">
                    <b-col cols="12" lg="6">
                        <div class="d-flex align-items-center justify-content-center">
                            <ConfigCard
                              class="mt-2"
                              :systemFeatures="systemFeatures"
                              :config="optimizedConfig"
                              :original-config="unoptimizedConfig"/>
                        </div>
                    </b-col>
                    <b-col cols="12" lg="6">
                        <ConfigValuesTable
                          class="mt-2"
                          :config="optimizedConfig"
                          :original-config="unoptimizedConfig"
                          :property-attributes="propertyAttributes">
                        </ConfigValuesTable>
                    </b-col>
                </b-row>
            </b-container>
        </template>
        <template #modal-footer="{ ok, cancel }">
            <b-button variant="outline-primary" @click="cancel()">
                Cancel
            </b-button>
            <b-button variant="success" :disabled="!optimizedConfigFound"
                      @click="ok()">
                Add
            </b-button>
        </template>
    </b-modal>
</template>

<script>

import ConfigCard from "./ConfigCard";
import ConfigValuesTable from "./ConfigValuesTable";

export default {
    name: "optimizationModal",

    components: {
        ConfigCard,
        ConfigValuesTable
    },

    data() {
        return {
            optimizationDistance: 1,
        }
    },

    props: {
        globalOptimization: {
            type: Boolean,
            required: true,
            default: false,
        },
        displayOptimizationModal: {
            type: Boolean,
            required: true,
            default: false,
        },
        selectedUnoptimizedConfigName: {
            type: String,
            required: true,
            default: "",
        },
        selectedOptimizationPropName: {
            type: String,
            required: true,
            default: "",
        },
        configNames: {
            type: Array,
            required: true,
            default: null
        },
        propNames: {
            type: Array,
            required: true,
            default: null
        },
        maxOptimizationDistance: {
            type: Number,
            required: true,
            default: 1
        },
        searchedForOptimizedConfig: {
            type: Boolean,
            required: true,
            default: false,
        },
        optimizedConfigFound: {
            type: Boolean,
            required: true,
            default: false
        },
        systemFeatures: {
            type: [Object, Array],
            required: true,
            default: null
        },
        optimizedConfig: {
            type: Object,
            required: true,
            default: null
        },
        unoptimizedConfig: {
            type: Object,
            required: false,
            default: null
        },
        propertyAttributes: {
            type: Object,
            required: false,
            default: null
        }
    },

    computed: {
        searchDisabled: function() {
            return this.selectedUnoptimizedConfigName === "" || this.selectedOptimizationPropName === "" || this.optimizationDistance < 1;
        },
        spinButtonDisabled: function() {
            return (this.selectedUnoptimizedConfigName === "");
        }
    },

    watch: {
        maxOptimizationDistance(newValue, oldValue){
            if (this.optimizationDistance > newValue) {
                this.optimizationDistance = newValue;
            }
        }
    },

    methods: {
        onChange(event) {
            this.$emit('update:displayOptimizationModal', event.value);
        },
        onChangeConfig(configName) {
            this.$emit('update:selectedUnoptimizedConfigName', configName);
        },
        onChangeProp(propName) {
            this.$emit('update:selectedOptimizationPropName', propName);
            if (this.globalOptimization) {
                this.$emit('change-global-prop', propName);
            }
        }
    }

}
</script>

<style scoped>

</style>

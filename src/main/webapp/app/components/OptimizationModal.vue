<template>
    <b-modal id="optiModal" :visible="displayOptimizationModal" @change="onChange" title="Optimization Search" size="lg" centered
             @ok="$emit('ok')" @hide="$emit('hide')">
        <template #default>
            <b-container>
                <b-row class="mb-2 align-items-center">
                    <b-col cols="4">Configuration:</b-col>
                    <b-col>
                        <b-form-select :value="selectedOptimizationConfigName" @change="onChangeConfig" :options="configNames">
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
                        </b-form-select></b-col>
                </b-row>
                <b-row class="mb-2 align-items-center">
                    <b-col cols="4">max. Difference:</b-col>
                    <b-col>
                        <b-form-spinbutton v-model="optimizationDistance" min="1" :max="maxOptimizationDistance"></b-form-spinbutton>
                    </b-col>
                </b-row>
                <b-row class="mb-2 px-3 justify-content-center">
                    <b-button class="w-100" variant="primary" :disabled="disableSearch"
                              @click="$emit('search-optimized-config', $event, selectedOptimizationConfigName, selectedOptimizationPropName, optimizationDistance)">
                        search
                    </b-button>
                </b-row>
                <b-row v-if="searchedForOptimizedConfig && !optimizedConfigFound">
                    <b-col><div class="text-danger">No better configuration found within max. difference range!</div></b-col>
                </b-row>
                <b-row v-if="searchedForOptimizedConfig && optimizedConfigFound">
                    <b-col cols="12" lg="6">
                        <div class="d-flex align-items-center justify-content-center">
                            <ConfigCard class="mt-2" :configurationFeatures="configurationFeatures" :config="optimizedConfig"/>
                        </div>
                    </b-col>
                    <b-col cols="12" lg="6">
                        <ConfigValuesTable class="mt-2" :config="optimizedConfig" :property-attributes="propertyAttributes"></ConfigValuesTable>
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
        displayOptimizationModal: {
            type: Boolean,
            required: true,
            default: false,
        },
        selectedOptimizationConfigName: {
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
        configurationFeatures: {
            type: [Object, Array],
            required: true,
            default: null
        },
        optimizedConfig: {
            type: Object,
            required: true,
            default: null
        },
        propertyAttributes: {
            type: Object,
            required: false,
            default: null
        }
    },

    computed: {
        disableSearch: function() {
            return this.selectedOptimizationConfigName === "" || this.selectedOptimizationPropName === "" || this.optimizationDistance < 1;
        }
    },

    methods: {
        onChange(event) {
            this.$emit('update:displayOptimizationModal', event.value);
        },
        onChangeConfig(configName) {
            this.$emit('update:selectedOptimizationConfigName', configName);
        },
        onChangeProp(propName) {
            this.$emit('update:selectedOptimizationPropName', propName);
        }
    }

}
</script>

<style scoped>

</style>

<template>
    <b-container fluid>
        <div class="mb-3 d-flex justify-content-between">
            <div><h5 class="m-0">absolute values</h5></div>
        </div>
        <br>
        <b-col>
            <template>
                <div class="d-flex justify-content-between">
                <b-table
                    :items="generatedConfigItems"
                    :fields="generateFields"
                    :no-border-collapse="noCollapse"
                    responsive
                >
                </b-table>
                </div>
            </template>
        </b-col>
        <hr>
        <div class="mb-3 d-flex justify-content-between">
            <div><h5 class="m-0">absolute differences</h5></div>
        </div>
        <b-row class="mb-2">
            <b-col>
                <div>
                    <b-input-group>
                        <b-form-select v-model="selectedConfigA" :options="generatedConfigItems.map(item => item.config)">
                            <template #first>
                                <b-form-select-option value="" disabled>-- Please select first configuration --</b-form-select-option>
                            </template>
                        </b-form-select>
                        <div class="d-flex align-items-center">
                            <font-awesome-icon class="mx-1 text-secondary" icon="minus"  fixed-width/>
                        </div>
                        <b-form-select v-model="selectedConfigB" :options="generatedConfigItems.map(item => item.config)">
                            <template #first>
                                <b-form-select-option value="" disabled>-- Please select second configuration --</b-form-select-option>
                            </template>
                        </b-form-select>
                    </b-input-group>
                </div>
            </b-col>
        </b-row>
        <b-row class="px-3" style="max-height: 24rem; overflow-y: auto">
            <template v-if="differenceArray !== []" v-for="(item) in differenceArray">
                <template v-if="item.propName === 'output' || item.propName === 'Geschwindigkeit'">
                    <b-col class="px-1 py-1" xl="4" lg="6" md="12">
                        <div v-if="item.valueDiff > 0">
                            <b-card class="text-center" bg-variant="success" text-variant="white">
                                <b-card-text>{{ item.valueDiff + item.unit }}</b-card-text>
                                <template #footer>
                                    <h6>{{ item.propName }}</h6>
                                </template>
                            </b-card>
                        </div>
                        <div v-else-if ="item.valueDiff < 0">
                            <b-card class="text-center" bg-variant="warning" text-variant="white">
                                <b-card-text>{{ item.valueDiff + item.unit }}</b-card-text>
                                <template #footer>
                                    <h6>{{ item.propName }}</h6>
                                </template>
                            </b-card>
                        </div>
                        <div v-else>
                            <b-card class="text-center" bg-variant="secondary" text-variant="white">
                                <b-card-text>{{ item.valueDiff + item.unit }}</b-card-text>
                                <template #footer>
                                    <h6>{{ item.propName }}</h6>
                                </template>
                            </b-card>
                        </div>
                    </b-col>
                </template>
                <template v-else>
                    <b-col class="px-1 py-1" xl="4" lg="6" md="12">
                        <div v-if="item.valueDiff > 0">
                            <b-card class="text-center" bg-variant="warning" text-variant="white">
                                <b-card-text>{{ item.valueDiff + item.unit }}</b-card-text>
                                <template #footer>
                                    <h6>{{ item.propName }}</h6>
                                </template>
                            </b-card>
                        </div>
                        <div v-else-if="item.valueDiff < 0">
                            <b-card class="text-center" bg-variant="success" text-variant="white">
                                <b-card-text>{{ item.valueDiff + item.unit }}</b-card-text>
                                <template #footer>
                                    <h6>{{ item.propName }}</h6>
                                </template>
                            </b-card>
                        </div>
                        <div v-else>
                            <b-card class="text-center" bg-variant="secondary" text-variant="white">
                                <b-card-text>{{ item.valueDiff + item.unit }}</b-card-text>
                                <template #footer>
                                    <h6>{{ item.propName }}</h6>
                                </template>
                            </b-card>
                        </div>
                    </b-col>
                </template>
            </template>
        </b-row>
    </b-container>
</template>

<script>


import { mapFields } from "vuex-map-fields";

export default {
    name: "PropertyCard",
    data() {
        return {
            noCollapse: false,
            selectedConfigA: "",
            selectedConfigB: "",
        }
    },

    computed: {
        ...mapFields(
            'configurationStore',
            [
                'configurations',
                'systemProperties'
            ]
        ),

        generatedConfigItems() {
            let configItems = [];
            for (let config of this.configurations) {
                if (config.properties != null && this.systemProperties != null) {
                    let item = {};
                    item.config = config.name;

                    const vm = this;

                    Object.keys(config.properties).forEach(function(key) {
                        const unit = vm.systemProperties[key].split(' ')[0];
                        item[key] = config.properties[key].toFixed(4) + unit;
                    });

                    configItems.push(item);
                }
            }
            return configItems;
        },

        generateFields() {
            let fields = [];
            if (this.generatedConfigItems.length > 0) {
                fields = Object.keys(this.generatedConfigItems[0]).map(key => {
                    return {
                        key: key,
                        sortable: key !== 'config',
                    }
                })
            }
            return fields;
        },

        differenceArray() {
            let differenceArray = [];
            if (this.selectedConfigA !== "" && this.selectedConfigB !== "") {
                const configA = this.configurations.find(conf => { return conf.name === this.selectedConfigA });
                const configB = this.configurations.find(conf => { return conf.name === this.selectedConfigB });

                if (configA != null && configB != null) {
                    const vm = this;

                    Object.keys(configA.properties).forEach(function (key) {
                        const differenceItem = {
                            propName: key,
                            valueDiff: (configA.properties[key] - configB.properties[key]).toFixed(4),
                            unit: vm.systemProperties[key].split(' ')[0],
                        }
                        differenceArray.push(differenceItem);
                    });
                } else {
                    if (configA == null) {
                        this.selectedConfigA = "";
                    }
                    if (configB == null) {
                        this.selectedConfigB = "";
                    }
                }
            }
            return differenceArray;
        },
    }
}
</script>


<style scoped>
    h6 {
        margin: 0
    }
</style>

<template>
    <div class="shadow-sm mr-0"
         style="width: 18rem;">
        <div class="config-card-header p-1">
            <div class="d-flex align-items-center">
                <h6 class="my-0 ml-3">{{config.name}}</h6>
            </div>
        </div>
        <div class="config-card-content p-1"
             style="min-height: 6rem; max-height: 16rem; overflow-y: auto">
            <div class="badges d-flex flex-wrap justify-content-center">
                <div v-for="chip in chips">
                    <Chip v-if="chip.type === 'binary-default'"
                          class="binary-chip m-1" :label="chip.featureName"/>
                    <Chip v-if="chip.type === 'removed'"
                          class="removed-chip m-1" :label="chip.featureName"/>
                    <Chip v-if="chip.type === 'added'"
                          class="added-chip m-1" :label="chip.featureName"/>
                    <Chip v-if="chip.type === 'numeric-default'"
                          class="numeric-chip m-1" :label="chip.featureName + ': ' + config[chip.featureName]"/>
                    <Chip v-if="chip.type === 'modified'"
                          class="modified-chip m-1" :label="chip.featureName + ': ' + config[chip.featureName]"/>
                </div>
            </div>
        </div>
        <div class="config-card-footer d-flex justify-content-center">
        </div>
    </div>
</template>

<script>
import Chip from './Chip';

export default {
    name: "ConfigCard",

    components: {
        Chip
    },

    props: {
        config: {
            type: Object,
            required: true,
        },
        configurationFeatures: {
            type: Object,
            required: true,
        },
        originalConfig: {
            type: Object,
            required: false,
            default: null,
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

            if (this.originalConfig !== null) {
                for (let binaryFeatureName of this.configurationFeatures.binaryFeatures) {
                    if (this.originalConfig[binaryFeatureName] === true && this.config[binaryFeatureName] === false) {
                        chips.push({featureName: binaryFeatureName, type: 'removed'});
                    } else if (this.originalConfig[binaryFeatureName] === false && this.config[binaryFeatureName] === true) {
                        chips.push({featureName: binaryFeatureName, type: 'added'});
                    } else if (this.config[binaryFeatureName]){
                        chips.push({featureName: binaryFeatureName, type: 'binary-default'});
                    }
                }

                for (let numericFeatureName of Object.keys(this.configurationFeatures.numericFeatures)) {
                    if (this.originalConfig[numericFeatureName] !== this.config[numericFeatureName]) {
                        chips.push({featureName: numericFeatureName, type: 'modified'});
                    } else {
                        chips.push({featureName: numericFeatureName, type: 'numeric-default'});
                    }
                }

            } else {
                for (let binaryFeatureName of this.configurationFeatures.binaryFeatures) {
                    if (this.config[binaryFeatureName]) {
                        chips.push({featureName: binaryFeatureName, type: 'binary-default'});
                    }
                }
                for (let numericFeatureName of Object.keys(this.configurationFeatures.numericFeatures)) {
                    chips.push({featureName: numericFeatureName, type: 'numeric-default'});
                }
            }

            return chips;
        }
    }

}
</script>

<style scoped>
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
</style>

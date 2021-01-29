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
                <template v-for="featureName in configurationFeatures.binaryFeatures">
                    <div>
                        <Chip v-if="config[featureName]"
                              class="binary-chip m-1" :label="featureName"/>
                    </div>
                </template>
                <template v-for="featureName in Object.keys(configurationFeatures.numericFeatures)">
                    <div>
                        <Chip class="numeric-chip m-1" :label="featureName+ ': ' + config[featureName]"/>
                    </div>
                </template>
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

    props: [
        "config",
        "configurationFeatures"
    ]


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
</style>

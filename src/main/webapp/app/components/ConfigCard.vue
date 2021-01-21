<template>
    <div class="config-card p-shadow-1"
         style="width: 18rem">
        <div class="config-card-header p-p-1">
            <div class="p-d-flex p-ai-center p-jc-between">
                <h6 class="p-my-0 p-ml-3">{{config.name}}</h6>
            </div>
        </div>
        <div class="config-card-content p-p-1"
             style="max-height: 16rem; overflow-y: auto">
            <div class="badges p-d-flex p-flex-wrap p-jc-center">
                <template v-for="featureName in configurationFeatures.binaryFeatures">
                    <div>
                        <Chip v-if="config[featureName]"
                              class="custom-chip p-m-1" :label="featureName"/>
                    </div>
                </template>
                <template v-for="featureName in Object.keys(configurationFeatures.numericFeatures)">
                    <div>
                        <Chip class="custom-chip p-m-1" :label="featureName+ ': ' + config[featureName]"/>
                    </div>
                </template>
            </div>
        </div>
        <div class="config-card-footer p-d-flex p-jc-center">
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

    .custom-chip {
        background: #2196F3;
        color: #fff;
    }
</style>

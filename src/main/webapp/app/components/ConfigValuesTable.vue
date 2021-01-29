<template>
    <div style="max-height: 16rem; overflow-y: auto">
        <b-table class="shadow-sm" :items="items" head-variant="light" small hover></b-table>
    </div>
</template>

<script>
export default {
    name: "ConfigValuesTable",

    props: {
        config: {
            type: Object,
            required: true,
            default: null,
        },
        propertyAttributes: {
            type: Object,
            required: false,
            default: null,
        }
    },

    computed: {
        items() {
            let items = null;
            if (this.propertyAttributes === null) {
                items = Object.entries(this.config.properties).map(([key, value]) => ({property: key,value: value.toFixed(2)}));

            } else {
                items = Object.entries(this.config.properties).map(([key, value]) => {
                    const unit = this.propertyAttributes[key].split(' ')[0];
                    return { property: key,value: value.toFixed(2) + unit }
                });
            }
            return items;
        }
    }

}
</script>

<style scoped>

</style>

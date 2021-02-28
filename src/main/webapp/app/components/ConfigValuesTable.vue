<template>
    <div style="max-height: 16rem; overflow-y: auto">
        <b-table class="shadow-sm" :fields="fields" :items="items" head-variant="light" small hover>
            <template #cell(value)="data">
                <template v-if="typeof data.value === 'object'">
                    {{ data.value.propValue }}<!--
                 --><template v-if="data.value.delta != null && data.value.diffColor != null"><!--
                     -->(<span :class="'text-' + data.value.diffColor">{{ data.value.delta }}</span>)<!--
                 --></template><!--
                 --><template v-else><!--
                     -->{{ data.value.delta }}<!--
                 --></template><!--
                 -->&nbsp{{ data.value.unit }}
                </template>
            </template>
        </b-table>
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
        originalConfig: {
            type: Object,
            required: false,
            default: null,
        },
        propertyAttributes: {
            type: Object,
            required: false,
            default: null,
        }
    },

    data() {
        return {
            fields: [
                'property',
                { key: 'value', label: 'value' }
            ]
        }
    },

    computed: {
        items() {
            let items = null;
            this.fields[1].label = 'value';

            if (this.propertyAttributes == null) {
                items = Object.entries(this.config.properties).map(([key, value]) => ({ property: key, value: value.toFixed(2) }));

            } else if (this.originalConfig == null || this.originalConfig.properties == null){
                items = Object.entries(this.config.properties).map(([key, value]) => {
                    const unit = this.propertyAttributes[key].split(' ')[0];

                    return {
                        property: key,
                        value: {
                            propValue: value.toFixed(2),
                            delta: null,
                            unit: unit
                        }
                    };
                });

            } else {
                this.fields[1].label = 'value (+/-)';

                items = Object.entries(this.config.properties).map(([key, value]) => {
                    let valueDifference = value - this.originalConfig.properties[key];

                    const unit = this.propertyAttributes[key].split(' ')[0];
                    const optimalDirection = this.propertyAttributes[key].split(' ')[1];

                    console.log(optimalDirection);

                    let item = {
                        property: key,
                        value: {
                            propValue: value.toFixed(2),
                            delta: valueDifference.toFixed(2),
                            unit: unit,
                            diffColor: 'secondary'
                        }
                    }

                    if (valueDifference > 0) {
                        item.value.delta = `+${valueDifference.toFixed(2)}`;

                        if (optimalDirection === '<') {
                            item.value.diffColor = 'danger';
                        } else if (optimalDirection === '>'){
                            item.value.diffColor = 'success';
                        }

                    } else if (valueDifference < 0){
                        if (optimalDirection === '<') {
                            item.value.diffColor = 'success';
                        } else if (optimalDirection === '>'){
                            item.value.diffColor = 'danger';
                        }
                    }

                    return item;
                });
            }
            return items;
        }
    }

}
</script>

<style scoped>

</style>

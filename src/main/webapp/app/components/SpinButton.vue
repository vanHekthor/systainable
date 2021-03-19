<template>
    <div class="spinbutton-container">
        <div class="step-down">
            <b-button class="p-1 no-outline" variant="link"
                      @click="decrease">
                <font-awesome-icon icon="minus" :style="{ color: '#9d9ea2' }" fixed-width/>
            </b-button>
        </div>
        <div class="value-display">
            <div>{{value}}</div>
        </div>
        <div class="step-up">
            <b-button class="p-1 no-outline" variant="link"
                      @click="increase">
                <font-awesome-icon icon="plus" :style="{ color: '#9d9ea2' }" fixed-width/>
            </b-button>
        </div>
    </div>
</template>

<script>
export default {
    name: "SpinButton",

    props: {
        value: {
            type: Number,
            required: false,
            default: 1,
        },
        step: {
            type: Number,
            required: false,
            default: 1,
        },
        stepFunction: {
            type: String,
            required: false,
            default: null,
        },
        min: {
            type: Number,
            required: false,
            default: null,
        },
        max: {
            type: Number,
            required: false,
            default: null,
        }
    },

    methods: {
        increase() {
            let step = this.step;
            if (this.stepFunction !== null) {
                const functionParts = this.stepFunction.split(" ");
                if (functionParts[1] === '+') {
                    step = parseFloat(functionParts[2]);
                    this.linearIncrease(step);
                } else if (functionParts[1] === '*') {
                    this.exponentialIncrease(parseFloat(functionParts[2]));
                }
            } else {
                this.linearIncrease(this.step);
            }
        },

        linearIncrease(step) {
            if (this.max !== null) {
                if (this.value + step <= this.max) {
                    this.$emit('update:value', this.value + step);
                }
            } else {
                this.$emit('update:value', this.value + step);
            }
        },

        exponentialIncrease(base) {
            if (this.max !== null) {
                if (this.value * base <= this.max) {
                    this.$emit('update:value', this.value * base);
                }
            } else {
                this.$emit('update:value', this.value * base);
            }
        },

        decrease() {
            let step = this.step;
            if (this.stepFunction !== null) {
                const functionParts = this.stepFunction.split(" ");
                if (functionParts[1] === '+') {
                    step = parseFloat(functionParts[2]);
                    this.linearDecrease(step);
                } else if (functionParts[1] === '*') {
                    this.exponentialDecrease(parseFloat(functionParts[2]));
                }
            } else {
                this.linearDecrease(this.step)
            }
        },

        linearDecrease(step) {
            if (this.min !== null) {
                if (this.value - step >= this.min) {
                    this.$emit('update:value', this.value - step);
                }
            } else {
                this.$emit('update:value', this.value - step);
            }
        },

        exponentialDecrease(base) {
            if (this.min !== null) {
                if (this.value / base >= this.min) {
                    this.$emit('update:value', this.value / base);
                }
            } else {
                this.$emit('update:value', this.value / base);
            }
        },
    }
}
</script>

<style scoped>
    .no-outline:focus,.no-outline:active {
        outline: none !important;
        box-shadow: none !important;
    }
    .spinbutton-container {
        display: flex;
        align-items: center;
    }
    .value-display {
        display: flex;
        min-width: 3rem !important;
        justify-content: center;
    }
    .step-down {
        width: 1.75rem;
    }
    .step-up {
        width: 1.75rem;
    }
</style>

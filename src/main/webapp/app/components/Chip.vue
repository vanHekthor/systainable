<template>
    <div :class="containerClass" v-if="visible">
        <slot>
            <img :src="image" v-if="image">
            <span :class="iconClass" v-else-if="icon"></span>
            <span @click="$emit('chip-clicked')">
                <div class="p-chip-text" v-if="label">
                     {{label}}
                </div>
            </span>
            <span v-if="removable" tabindex="-1" :class="removeIconClass"
                  @click="close" @keydown.enter="close"></span>
        </slot>
    </div>
</template>

<script>
export default {
    emits: ['remove'],
    props: {
        label: {
            type: String,
            default: null
        },
        icon: {
            type: String,
            default: null
        },
        image: {
            type: String,
            default: null
        },
        removable: {
            type: Boolean,
            default: false
        },
        removeIcon: {
            type: String,
            default: 'pi pi-times-circle'
        }
    },
    data() {
        return {
            visible: true
        }
    },

    methods: {
        close(event) {
            this.$emit('remove', event);
        }
    },

    computed: {
        containerClass() {
            return ['p-chip p-component', {
                'p-chip-image': this.image != null
            }];
        },
        iconClass() {
            return ['p-chip-icon', this.icon];
        },
        removeIconClass() {
            return ['pi-chip-remove-icon', this.removeIcon];
        }
    }
}
</script>

<style scoped>
.p-chip {
    display: inline-flex;
    align-items: center;
}
.p-chip-text {
    line-height: 1.5;
    font-size: 0.8rem;
}

.p-chip-icon.pi {
    line-height: 1.5;
}
.pi-chip-remove-icon {
    line-height: 1.5;
    cursor: pointer;
}
.p-chip img {
    border-radius: 50%;
}
</style>

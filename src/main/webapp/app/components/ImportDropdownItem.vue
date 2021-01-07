<template>
    <b-dropdown-item-button @click="handleImport">
        <font-awesome-icon icon="file-upload" class="mr-1" :style="{ color: '#6c757d' }" fixed-width/>{{ label }}
    </b-dropdown-item-button>
</template>

<script>
export default {
    name: "UploadDropdownItem",

    props: {
        label: {
            type: String,
            required: false,
            default: "Load",
        },
        systemName: {
            type: String,
            required: true,
            default: "",
        },
        featureNames: {
            type: Array,
            required: true,
            default: [],
        },
        fileCheck: {
            type: Boolean,
            required: false,
            default: false,
        },
        validObject: {
            type: Object,
            required: false,
            default: null,
        }
    },

    methods: {
        handleImport() {
            const input = document.createElement("input");
            input.setAttribute("type", "file");
            input.click();

            input.onchange= () => {
                const file = input.files;
                if (file.length <= 0) {
                    return false;
                }

                const fr = new FileReader();
                fr.onload = e => {
                    const result = JSON.parse(e.target.result);
                    if (this.fileCheck) {
                        if ((result.systemName === this.systemName) && (this.validateData(result.data))) {
                            this.loadData(result.data);
                            this.makeToast('success')
                            return;
                        }
                    } else {
                        this.loadData(result.data);
                        return;
                    }
                    this.makeToast('danger')
                }
                fr.readAsText(file.item(0));
            }
        },

        validateData: function(data) {
            console.log("start validating");

            for (let object of data) {
                if (!this.checkKeys(object)) {
                    return false;
                }
            }

            return true;
        },

        checkKeys: function(object) {
            let featureNames = this.featureNames;
            featureNames.sort();
            const keys = Object.keys(object).sort();

            return JSON.stringify(featureNames) === JSON.stringify(keys);
        },

        loadData(data) {
            this.$emit('load-data', data);
        },

        makeToast(variant) {
            if (variant === 'danger') {
                this.$bvToast.toast('Selected system and/or feature names do not match.', {
                    title: 'Unable to load file!',
                    variant: variant,
                    solid: true
                })
            } else if (variant === 'success') {
                this.$bvToast.toast('File has been loaded successfully.', {
                    title: 'Success!',
                    variant: variant,
                    solid: true
                })
            }
        }
    }
}
</script>

<style scoped>

</style>

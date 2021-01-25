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
        binaryFeatures: {
            type: Array,
            required: true,
            default: [],
        },
        numericFeatures: {
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
                    try {
                        const result = JSON.parse(e.target.result);
                        if (this.fileCheck) {
                            if (this.validateFile(result)) {
                                this.loadData(result.data);
                                this.makeToast('success', null);
                            }
                        } else {
                            this.loadData(result.data);
                        }
                    } catch (e) {
                        if (e.name === "SyntaxError") {
                            this.makeToast('danger', "File does not comply with JSON syntax.");
                        } else {
                            this.makeToast('danger', e.message);
                        }
                    }
                }
                fr.readAsText(file.item(0));
            }
        },

        validateFile: function(file) {
            if (file.systemName !== this.systemName) {
                throw new Error(
                    `The software system "${file.systemName}" defined in the file does not match the selected system "${this.systemName}".`
                );
            }

            for (let object of file.data) {
                this.checkKeys(object);
                this.checkValueTypes(object);
            }

            return true;
        },

        checkKeys: function(object) {
            let featureNames = ['name'].concat([...this.binaryFeatures].concat(this.numericFeatures));

            featureNames.sort();
            const keys = Object.keys(object).sort();

            if (JSON.stringify(featureNames) !== JSON.stringify(keys)) {
                throw Error('Keys in the file do not match valid configuration attributes.')
            }

            return true;
        },

        checkValueTypes: function(object) {
            if (typeof object['name'] !== 'string') {
                throw Error(`Configuration name should be a string.`);
            }
            for (let key of this.binaryFeatures) {
                if (typeof object[key] !== 'boolean') {
                    throw Error(`Feature "${key}" of configuration "${object['name']}" should be boolean.`);
                }
            }
            for (let key of this.numericFeatures) {
                if (typeof object[key] !== 'number') {
                    throw Error(`Feature "${key}" of configuration "${object['name']}" should be a number.`);
                }
            }
            return true;
        },

        loadData(data) {
            this.$emit('load-data', data);
        },

        makeToast(variant, message) {
            if (variant === 'danger' && message !== null){
                this.$bvToast.toast(message, {
                    title: 'Unable to load file!',
                    variant: variant,
                    solid: true,
                    autoHideDelay: 12000
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

<template>
    <div>
        <b-button v-b-modal.filename-modal variant="outline-primary">
            <font-awesome-icon icon="file-export" fixed-width/>
            {{label}}
        </b-button>
        <b-modal id="filename-modal" centered title="Enter a filename"
                 @ok="handleDownload">
            <b-form-input type="text" :value="fileName" v-model="fileName"></b-form-input>
        </b-modal>
    </div>
</template>

<script>
export default {
    name: "ExportButton",

    props: {
        label: {
            type: String,
            required: false,
            default: "Export",
        },
        systemName: {
            type: String,
            required: true,
            default: "",
        },
        fileName: {
            type: String,
            required: false,
            default: "config_export",
        },
        data: {
            type: Array,
            required: true,
            default: () => [],
        },
        fields: {
            type: Array,
            required: false,
            default: () => [],
        }
    },

    computed: {
        createContent() {
            const exportObject = {
                systemName: this.systemName,
                data: this.data
            }

            return JSON.stringify(exportObject);
        },
    },

    methods: {
        handleDownload() {
            let content = this.createContent;

            if (content === null) {
                this.$emit("error");
                return;
            }

            const blob = new Blob([content], {
                type: 'application/json'
            });
            const link = document.createElement("a");
            link.href = window.URL.createObjectURL(blob);
            link.download = `${this.fileName}.json`;
            link.click();
            this.$emit("success");
        },
    }

}
</script>

<style scoped>

</style>

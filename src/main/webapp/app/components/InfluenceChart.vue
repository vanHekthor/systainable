<script>
import { HorizontalBar, mixins } from 'vue-chartjs';
const { reactiveProp } = mixins;

export default {
    name: "InfluenceChart",
    extends: HorizontalBar,
    mixins: [reactiveProp],
    props: {
        chartData: {
            type: Object,
            required: true,
            default: null
        },
        options: {
            type: Object,
            required: true,
            default: null
        }
    },
    mounted() {
        this.renderChart(this.chartData, this.options)
    }
};
</script>

<style scoped>
</style>

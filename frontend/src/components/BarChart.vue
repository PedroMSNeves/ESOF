<template>
    <div  v-if="years.length ==2">
                <Bar :chartData=" {
                  labels: [String(this.years[1]), String(this.years[0])],
                  datasets: [ { label: label[0],data:  [this.stats1[1],this.stats1[0]] , backgroundColor: ['#d60001']},
                              { label: label[1],data:  [this.stats2[1],this.stats2[0]] , backgroundColor: ['#006ca2']},
                              { label: label[2],data:  [this.stats3[1],this.stats3[0]] , backgroundColor: ['#0b6e00']}]
                }" :chartOptions="chartOptions" />
    </div>
    <div v-else-if="years.length ==3">
                <Bar :chartData=" {
                  labels: [String(this.years[2]), String(this.years[1]), String(this.years[0])],
                  datasets: [ { label: label[0],data:  [this.stats1[2],this.stats1[1] ,this.stats1[0]] , backgroundColor: ['#d60001']},
                              { label: label[1],data:  [this.stats2[2],this.stats2[1] ,this.stats2[0]] , backgroundColor: ['#006ca2']},
                              { label: label[2],data:  [this.stats3[2],this.stats3[1] ,this.stats3[0]] , backgroundColor: ['#0b6e00']}]
                }" :chartOptions="chartOptions" />
    </div>
</template>

<script lang="ts">
import {Prop, Vue, Watch} from 'vue-property-decorator';
import { Bar } from 'vue-chartjs/legacy'
import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale } from 'chart.js'

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

export default {

  props: {
    stats1: {type:Array, default: () => [] },
    stats2: {type:Array, default: () => [] },
    stats3: {type:Array, default: () => [] },
    years: {type:Array, default: () => [] },
    label: {type:Array, default: () => [] }
    },
  name: 'BarChart',
  components: { Bar },
  data() {
    return {
      chartOptions: {
        maintainAspectRatio: false
      }
    }
  }
}
</script>
<template>
    <div  v-if="years.length ==2">
                <Bar :chartData=" {
                  labels: [String(this.years[0]), String(this.years[1])],
                  datasets: [ { label: 'Total Number of Students', data:  [this.stats1[0],this.stats1[1] ], backgroundColor: ['#d60001']},
                              { label: 'Students who Solved > 75% of Questions',data:  [this.stats2[0],this.stats2[1]] , backgroundColor: ['#006ca2']},
                              { label: 'Students who Solved >= 3 Quizzes ',data: [this.stats3[0],this.stats3[1]] , backgroundColor: ['#0b6e00']}]
                }" :chartOptions="chartOptions" />
    </div>
    <div v-else-if="years.length ==3">
                <Bar :chartData=" {
                  labels: [String(this.years[0]), String(this.years[1]), String(this.years[2])],
                  datasets: [ { label: 'Total Number of Students', data:  [this.stats1[0],this.stats1[1] ,this.stats1[2]], backgroundColor: ['#d60001']},
                              { label: 'Students who Solved > 75% of Questions',data:  [this.stats2[0],this.stats2[1] ,this.stats2[2]] , backgroundColor: ['#006ca2']},
                              { label: 'Students who Solved >= 3 Quizzes ',data: [this.stats3[0],this.stats3[1] ,this.stats3[2]] , backgroundColor: ['#0b6e00']}]
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
    years: {type:Array, default: () => [] }
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
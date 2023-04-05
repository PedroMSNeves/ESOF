<template>
  <div>
    <Bar :chartData="chartData" :chartOptions="chartOptions" />
  </div>
</template>

<script lang="ts">
import {Prop, Vue, Watch} from 'vue-property-decorator';
import { Bar } from 'vue-chartjs/legacy'
import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale } from 'chart.js'

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

export default {

  props: {
    stats1: Array,
    stats2: Array,
    stats3: Array,
    },
  name: 'BarChart',
  components: { Bar },
  data() {
    return {

      chartData: {
        labels: ['January', 'February', 'March'],
        datasets: [ { label: 'Total Number of Students', data:  this.props.stats1, backgroundColor: ['#d60001']},
                    { label: 'Students who Solved > 75% of Questions',data:  this.props.stats2  , backgroundColor: ['#006ca2']},
                    { label: 'Students who Solved >= 3 Quizzes ',data: this.props.stats3 , backgroundColor: ['#0b6e00']}]
      },
      chartOptions: {
        maintainAspectRatio: false
      }
    }
  }
}
</script>
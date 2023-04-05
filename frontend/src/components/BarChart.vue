<template>
  <div>
    <Bar :chartData="chartData" :chartOptions="chartOptions" />
  </div>
</template>

<script lang="ts">
import { Bar } from 'vue-chartjs/legacy'
import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale } from 'chart.js'

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

export default {

  name: 'BarChart',
  components: { Bar },
  data() {
    return {

      chartData: {
        labels: ['January', 'February', 'March'],
        datasets: [ { label: 'Total Number of Students', data: [40, 20, 12] , backgroundColor: ['#d60001']},
                    { label: 'Students who Solved > 75% of Questions',data: [10, 21, 34] , backgroundColor: ['#006ca2']},
                    { label: 'Students who Solved >= 3 Quizzes ',data: [39, 8, 23] , backgroundColor: ['#0b6e00']}]
      },
      chartOptions: {
        maintainAspectRatio: false
      }
    }
  }
}
</script>
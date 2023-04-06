<template>
  <div class="container">
    <h2>Statistics for this course execution</h2>
    <div v-if="teacherDashboard != null" class="stats-container">
      <div v-if="teacherDashboard.studentStats == null" class="stats-container">
            <div class="items">
                <div ref="numStudents" class="icon-wrapper">
                    <animated-number :number="0" />
                </div>
                <div class="project-name">
                    <p>Number of Students</p>
                </div>
            </div>
            <div class="items">
                <div ref="numMore75CorrectQuestions" class="icon-wrapper">
                    <animated-number :number="0" />
                </div>
                <div class="project-name">
                    <p>Number of Students who Solved > 75% Questions</p>
                </div>
            </div>
            <div class="items">
                <div ref="numAtLeast3Quizzes" class="icon-wrapper">
                    <animated-number :number="0" />
                </div>
                <div class="project-name">
                    <p>Number of Students who Solved >= 3 Quizzes</p>
                </div>
            </div>
        </div>
        <div v-else-if="teacherDashboard.studentStats != null" class="stats-container">
            <div class="items">
                <div ref="numStudents" class="icon-wrapper">
                    <animated-number :number="teacherDashboard.studentStats[0].numStudents" />
                </div>
                <div class="project-name">
                    <p>Number of Students</p>
                </div>
            </div>
            <div class="items">
                <div ref="numMore75CorrectQuestions" class="icon-wrapper">
                    <animated-number :number="teacherDashboard.studentStats[0].numMore75CorrectQuestions" />
                </div>
                <div class="project-name">
                    <p>Number of Students who Solved > 75% Questions</p>
                </div>
            </div>
            <div class="items">
                <div ref="numAtLeast3Quizzes" class="icon-wrapper">
                    <animated-number :number="teacherDashboard.studentStats[0].numAtLeast3Quizzes" />
                </div>
                <div class="project-name">
                    <p>Number of Students who Solved >= 3 Quizzes</p>
                </div>
            </div>
        </div>
    </div>
    <div v-if="teacherDashboard.studentStats.length >1" class="bar-chart">
            <BarChart :stats1="stats1" :stats2="stats2" :stats3="stats3" :years="years"/>
    </div>

  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue, Watch} from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import AnimatedNumber from '@/components/AnimatedNumber.vue';
import TeacherDashboard from '@/models/dashboard/TeacherDashboard';
import TeacherDashboardStudentStats from '@/models/dashboard/TeacherDashboardStudentStats';
import BarChart from '@/components/BarChart.vue';

@Component({
  components: { AnimatedNumber, BarChart },
})

export default class TeacherStatsView extends Vue {
  @Prop() readonly dashboardId!: number;
  teacherDashboard: TeacherDashboard | null = null;
  years : number[] = [];
  stats1: number[] = [];
  stats2: number[] = [];
  stats3: number[] = [];
  async created() {
    await this.$store.dispatch('loading');
    try {
      this.teacherDashboard = await RemoteServices.getTeacherDashboard();
      for( var i =0; i< this.teacherDashboard.studentStats.length; i++){
        this.years.push(this.teacherDashboard.studentStats[i].courseExecutionYear);
        this.stats1.push(this.teacherDashboard.studentStats[i].numStudents);
        this.stats2.push(this.teacherDashboard.studentStats[i].numMore75CorrectQuestions);
        this.stats3.push(this.teacherDashboard.studentStats[i].numAtLeast3Quizzes);
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped>
.stats-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;

  .items {
    background-color: rgba(255, 255, 255, 0.75);
    color: #1976d2;
    border-radius: 5px;
    flex-basis: 25%;
    margin: 20px;
    cursor: pointer;
    transition: all 0.6s;
  }

  .bar-chart {
    background-color: rgba(255, 255, 255, 0.90);
    height: 400px;
    width: 700px;
  }
}

.icon-wrapper,
.project-name {
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper {
  font-size: 100px;
  transform: translateY(0px);
  transition: all 0.6s;
}

.icon-wrapper {
  align-self: end;
}

.project-name {
  align-self: start;
}

.project-name p {
  font-size: 24px;
  font-weight: bold;
  letter-spacing: 2px;
  transform: translateY(0px);
  transition: all 0.5s;
}

.items:hover {
  border: 3px solid black;

  & .project-name p {
    transform: translateY(-10px);
  }

  & .icon-wrapper i {
    transform: translateY(5px);
  }
}
</style>

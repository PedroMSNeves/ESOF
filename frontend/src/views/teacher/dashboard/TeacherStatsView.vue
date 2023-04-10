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

        <div v-if="teacherDashboard.quizStats == null" class="stats-container">
            <div class="items">
                <div ref="numQuizzes" class="icon-wrapper">
                    <animated-number :number="0" />
                </div>
                <div class="project-name">
                    <p>Number of Quizzes</p>
                </div>
            </div>
            <div class="items">
                <div ref="numUniqueAnsweredQuizzes" class="icon-wrapper">
                    <animated-number :number="0" />
                </div>
                <div class="project-name">
                    <p>Number of Unique Quizzes Solved</p>
                </div>
            </div>
            <div class="items">
                <div ref="averageQuizzesSolved" class="icon-wrapper">
                    <animated-number :number="0" />
                </div>
                <div class="project-name">
                    <p>Average Quizzes Solved</p>
                </div>
            </div>
        </div>
        <div v-else-if="teacherDashboard.quizStats != null" class="stats-container">
            <div class="items">
                <div ref="numQuizzes" class="icon-wrapper">
                    <animated-number :number="teacherDashboard.quizStats[0].numQuizzes" />
                </div>
                <div class="project-name">
                    <p>Number of Quizzes</p>
                </div>
            </div>
            <div class="items">
                <div ref="numUniqueAnsweredQuizzes" class="icon-wrapper">
                    <animated-number :number="teacherDashboard.quizStats[0].numUniqueAnsweredQuizzes" />
                </div>
                <div class="project-name">
                    <p>Number of Unique Quizzes Solved</p>
                </div>
            </div>
            <div class="">
                <div ref="averageQuizzesSolved" class="icon-wrapper">
                    <animated-number :number="teacherDashboard.quizStats[0].averageQuizzesSolved" />
                </div>
                <div class="project-name">
                    <p>Average Quizzes Solved</p>
                </div>
            </div>
        </div>
      </div>
      <div v-if="teacherDashboard.studentStats.length >1" class="bar-chart">
              <BarChart :stats1="studentStats1" :stats2="studentStats2" :stats3="studentStats3" :years="years" :label="['Total Number of Students','Students who Solved > 75% of Questions','Students who Solved >= 3 Quizzes']"/>
              </div>
      <div v-if="teacherDashboard.quizStats.length >1" class="bar-chart">
              <BarChart :stats1="quizStats1" :stats2="quizStats2" :stats3="quizStats3" :years="years" :label="['Total Number of Quizzes','Number of Unique Quizzes Solved','Average Quizzes Solved']"/>
              </div>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue, Watch} from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import AnimatedNumber from '@/components/AnimatedNumber.vue';
import TeacherDashboard from '@/models/dashboard/TeacherDashboard';
import TeacherDashboardStudentStats from '@/models/dashboard/TeacherDashboardStudentStats';
import TeacherDashboardQuizStats from '@/models/dashboard/TeacherDashboardQuizStats.js';
import BarChart from '@/components/BarChart.vue';

@Component({
  components: { AnimatedNumber, BarChart },
})

export default class TeacherStatsView extends Vue {
  @Prop() readonly dashboardId!: number;
  teacherDashboard: TeacherDashboard | null = null;
  years : number[] = [];
  studentStats1: number[] = [];
  studentStats2: number[] = [];
  studentStats3: number[] = [];
  quizStats1 : number[] = [];
  quizStats2 : number[] = [];
  quizStats3 : number[] = [];
  async created() {
    await this.$store.dispatch('loading');
    try {
      this.teacherDashboard = await RemoteServices.getTeacherDashboard();
      for( var i =0; i< this.teacherDashboard.studentStats.length; i++){
        this.years.push(this.teacherDashboard.studentStats[i].courseExecutionYear);
        this.studentStats1.push(this.teacherDashboard.studentStats[i].numStudents);
        this.studentStats2.push(this.teacherDashboard.studentStats[i].numMore75CorrectQuestions);
        this.studentStats3.push(this.teacherDashboard.studentStats[i].numAtLeast3Quizzes);
        this.quizStats1.push(this.teacherDashboard.quizStats[i].numQuizzes);   
        this.quizStats2.push(this.teacherDashboard.quizStats[i].numUniqueAnsweredQuizzes);   
        this.quizStats3.push(this.teacherDashboard.quizStats[i].averageQuizzesSolved);   
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

import TeacherDashboardStudentStats from '@/models/dashboard/TeacherDashboardStudentStats';
import TeacherDashboardQuizStats from '@/models/dashboard/TeacherDashboardQuizStats';
import TeacherDashboardQuestionStats from '@/models/dashboard/TeacherDashboardQuestionStats';

export default class TeacherDashboard {
  id!: number;
  studentStats: TeacherDashboardStudentStats[] = [];
  quizStats: TeacherDashboardQuizStats[] = [];
  questionsStats: TeacherDashboardQuestionStats[] = [];

  constructor(jsonObj?: TeacherDashboard) {
    if (jsonObj) {
      this.id = jsonObj.id;
      if (jsonObj.studentStats) {
        this.studentStats = jsonObj.studentStats.map(
        (studentStats: TeacherDashboardStudentStats) => new TeacherDashboardStudentStats(studentStats));
      }
      if(jsonObj.quizStats) {
        this.quizStats = jsonObj.quizStats.map(
          (quizStats: TeacherDashboardQuizStats) => new TeacherDashboardQuizStats(quizStats));
      }
      if(jsonObj.questionsStats) {
          this.questionsStats = jsonObj.questionsStats.map(
            (questionsStats: TeacherDashboardQuestionStats) => new TeacherDashboardQuestionStats(questionsStats));
      }
    }
  }
}

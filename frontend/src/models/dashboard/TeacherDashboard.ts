import TeacherDashboardStudentStats from '@/models/dashboard/TeacherDashboardStudentStats';
import TeacherDashboardQuizStats from '@/models/dashboard/TeacherDashboardQuizStats';

export default class TeacherDashboard {
  id!: number;
  studentStats: TeacherDashboardStudentStats[] = [];
  quizStats: TeacherDashboardQuizStats[] = [];

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
    }
  }
}

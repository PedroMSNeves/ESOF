import TeacherDashboardStudentStats from '@/models/dashboard/TeacherDashboardStudentStats';
import TeacherDashboardQuizStats from '@/models/dashboard/TeacherDashboardQuizStats';

export default class TeacherDashboard {
  id!: number;
  studentStats: TeacherDashboardStudentStats[] = [];
<<<<<<< HEAD
  quizStats: TeacherDashboardQuizStats[] = [];
=======
  quizStats: TeacherDashboardQuizStats[] = []
>>>>>>> 53d25f22d0d7f04033735074cd956380d5b7b1bc

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

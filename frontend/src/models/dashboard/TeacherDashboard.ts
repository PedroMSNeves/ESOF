import TeacherDashboardStudentStats from '@/models/dashboard/TeacherDashboardStudentStats';
<<<<<<< HEAD
import TeacherDashboardQuizStats from '@/models/dashboard/TeacherDashboardQuizStats';
=======
import QuizStats from "./QuizStats";
>>>>>>> Closes #134 : Modificate TeacherDashboard.ts

export default class TeacherDashboard {
  id!: number;
  studentStats: TeacherDashboardStudentStats[] = [];
<<<<<<< HEAD
  quizStats: TeacherDashboardQuizStats[] = [];
=======
  quizStats: QuizStats[] = []
>>>>>>> Closes #134 : Modificate TeacherDashboard.ts

  constructor(jsonObj?: TeacherDashboard) {
    if (jsonObj) {
      this.id = jsonObj.id;
      if (jsonObj.studentStats) {
        this.studentStats = jsonObj.studentStats.map(
        (studentStats: TeacherDashboardStudentStats) => new TeacherDashboardStudentStats(studentStats));
      }
      if(jsonObj.quizStats) {
        this.quizStats = jsonObj.quizStats.map(
<<<<<<< HEAD
          (quizStats: TeacherDashboardQuizStats) => new TeacherDashboardQuizStats(quizStats));
=======
          (quizStats: QuizStats) => new QuizStats(quizStats));
>>>>>>> Closes #134 : Modificate TeacherDashboard.ts
      }
    }
  }
}

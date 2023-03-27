import TeacherDashboardStudentStats from '@/models/dashboard/TeacherDashboardStudentStats';

export default class TeacherDashboard {
  id!: number;
  studentStats: TeacherDashboardStudentStats[] = [];

  constructor(jsonObj?: TeacherDashboard) {
    if (jsonObj) {
      this.id = jsonObj.id;
      if (jsonObj.studentStats) {
        this.studentStats = jsonObj.studentStats.map(
        (studentStats: TeacherDashboardStudentStats) => new TeacherDashboardStudentStats(studentStats));
      }
    }
  }
}

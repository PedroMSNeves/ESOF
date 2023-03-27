export default class TeacherDashboardStudentStats {
  id!: number;
  numStudent!: number;
  numMore75CorrectQuestions!: number;
  numAtLeast3Quizzes!: number;
  courseExecutionYear!: number;

  constructor(jsonObj?: TeacherDashboardStudentStats) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.numStudent = jsonObj.numStudent;
      this.numMore75CorrectQuestions = jsonObj.numMore75CorrectQuestions;
      this.numAtLeast3Quizzes = jsonObj.numAtLeast3Quizzes;
      this.courseExecutionYear = jsonObj.courseExecutionYear;
    }
  }
}

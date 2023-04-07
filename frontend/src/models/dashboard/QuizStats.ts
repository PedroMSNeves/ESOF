export default class QuizStats{
    id!: number;
    numQuizzes!: number;
    uniqueQuizzesSolved!:number
    averageQuizzesSolved!:number
    courseExecutionYear!: number;
  
    constructor(jsonObj?: QuizStats) {
      if (jsonObj) {
        this.id = jsonObj.id;
        this.numQuizzes = jsonObj.numQuizzes;
        this.uniqueQuizzesSolved=jsonObj.uniqueQuizzesSolved;
        this.averageQuizzesSolved=jsonObj.averageQuizzesSolved;
        this.courseExecutionYear = jsonObj.courseExecutionYear;
      }
    }
}
  
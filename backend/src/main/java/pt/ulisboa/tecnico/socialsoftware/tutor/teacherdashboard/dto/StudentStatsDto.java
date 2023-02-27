package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

//import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats;
import java.io.Serializable;

public class StudentStatsDto implements Serializable {
        private Integer id=0;
        private Integer numStudent =0;
        private Integer numMore75CorrectQuestions =0;
        private Integer numAtLeast3Quizzes =0;


        public StudentStatsDto() {
        }

        public StudentStatsDto(StudentStats studentStats) {
                id = studentStats.getId();
                numStudent = studentStats.getNumStudent();
                numMore75CorrectQuestions = studentStats.getNumMore75CorrectQuestions();
                numAtLeast3Quizzes = studentStats.getNumAtLeast3Quizzes();
        }

        public Integer getId() {
                return id;
        }

        public void setId(Integer id) {
                this.id = id;
        }

        public Integer getNumStudent(){
                return this.numStudent;
        }
        public void setNumStudent(int numstudents){
                this.numStudent= numstudents;
        }
        public Integer getNumMore75CorrectQuestions(){
                return this.numMore75CorrectQuestions;
        }
        public void setNumMore75CorrectQuestions(int value){
                this.numMore75CorrectQuestions= value;
        }
        public Integer getNumAtLeast3Quizzes(){
                return this.numAtLeast3Quizzes;
        }
        public void setNumAtLeast3Quizzes(int value){
                this.numAtLeast3Quizzes= value;
        }
        @Override
        public String toString() {
                return "StudentStatsDto{" +
                        "id=" + getId() +
                        ", numStudent=" + getNumStudent() +
                        ", numMore75CorrectQuestions=" + getNumMore75CorrectQuestions() +
                        ", numAtLeast3Quizzes=" + getNumAtLeast3Quizzes() + '}';
        }
}
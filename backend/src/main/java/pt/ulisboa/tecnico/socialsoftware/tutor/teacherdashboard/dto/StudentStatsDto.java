package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats;
import java.io.Serializable;

public class StudentStatsDto implements Serializable {
        private Integer id;
        private int numStudent;
        private int numMore75CorrectQuestions;
        private int numAtLeast3Quizzes;


        public StudentStatsDto() {
        }

        public StudentStatsDto(StudentStats studentStats) {
                this.setId(studentStats.getId());
                this.setNumStudent(studentStats.getNumStudent());
                this.setNumMore75CorrectQuestions(studentStats.getNumMore75CorrectQuestions());
                this.setNumAtLeast3Quizzes(getNumAtLeast3Quizzes());
        }

        public Integer getId() {
                return id;
        }
        public void setId(Integer id) {
                this.id = id;
        }

        public int getNumStudent(){
                return this.numStudent;
        }
        public void setNumStudent(int nt){
                this.numStudent= nt;
        }

        public int getNumMore75CorrectQuestions(){
                return this.numMore75CorrectQuestions;
        }
        public void setNumMore75CorrectQuestions(int value){
                this.numMore75CorrectQuestions= value;
        }

        public int getNumAtLeast3Quizzes(){
                return this.numAtLeast3Quizzes;
        }
        public void setNumAtLeast3Quizzes(int value){
                this.numAtLeast3Quizzes= value;
        }

        @Override
        public String toString() {
                return "StudentStatsDto{" +
                        "id=" + this.getId() +
                        ", numStudent=" + this.getNumStudent() +
                        ", numMore75CorrectQuestions=" + this.getNumMore75CorrectQuestions() +
                        ", numAtLeast3Quizzes=" + this.getNumAtLeast3Quizzes() + '}';
        }
}
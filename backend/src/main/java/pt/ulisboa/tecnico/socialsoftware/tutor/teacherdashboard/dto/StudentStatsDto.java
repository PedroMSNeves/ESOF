package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;

public class StudentStatsDto {
        private Integer numStudentStats =0;
        private Integer numMore75CorrectQuestions =0;
        private Integer numAtLeast3Quizzes =0;

        public Integer getNumStudentStats(){ return numStudentStats; }

}
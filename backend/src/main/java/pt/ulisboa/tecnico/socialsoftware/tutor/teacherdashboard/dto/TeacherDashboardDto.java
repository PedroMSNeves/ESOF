package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto.StudentStatsDto;


import java.util.List;
import java.util.stream.Collectors;

public class TeacherDashboardDto {
    private Integer id;
    private Integer numberOfStudents;

    private List<StudentStatsDto> studentStatsDtos;
    public TeacherDashboardDto() {
    }

    public TeacherDashboardDto(TeacherDashboard teacherDashboard) {
        this.id = teacherDashboard.getId();
        // For the number of students, we consider only active students
        this.numberOfStudents = teacherDashboard.getCourseExecution().getNumberOfActiveStudents();

        setStudentStatsDtos(teacherDashboard.getStudentStats().stream().map(st -> new StudentStatsDto(st)).collect(Collectors.toList()));
    }
    public List<StudentStatsDto> getStudentStatsDtos() {
        return studentStatsDtos;
    }
    public void setStudentStatsDtos(List<StudentStatsDto> studentStatsDtos) {
        this.studentStatsDtos = studentStatsDtos;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumberOfStudents() {
        return numberOfStudents;
    }

    public void setNumberOfStudents(Integer numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }

    @Override
    public String toString() {
        return "TeacherDashboardDto{" +
                "id=" + id +
                ", numberOfStudents=" + this.getNumberOfStudents() + ", studentStatsDtos=" + this.getStudentStatsDtos() +
                "}";
    }
}

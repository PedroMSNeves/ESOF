package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto.StudentStatsDto;


import java.util.List;
import java.util.stream.Collectors;

public class TeacherDashboardDto {
    private Integer id;
    private List<StudentStatsDto> studentStatsDtos;
    public TeacherDashboardDto() {
    }

    public TeacherDashboardDto(TeacherDashboard teacherDashboard) {
        this.id = teacherDashboard.getId();
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

    @Override
    public String toString() {
        return "TeacherDashboardDto{" +
                "id=" + id +
                ", studentStatsDtos=" + this.getStudentStatsDtos() +
                "}";
    }
}

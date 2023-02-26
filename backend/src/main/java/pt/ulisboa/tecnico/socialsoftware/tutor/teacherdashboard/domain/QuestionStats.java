package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.StudentDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import javax.persistence.*;
import java.util.stream.Collectors;
import java.util.*;

@Entity
public class QuestionStats implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer numQuestionsAvailable = 0;
    private Integer numQuestionsAnsweredUniq = 0;
    private Double averageQuestionsAnsweredUniq = 0.0;

    @OneToOne
    private CourseExecution courseExecution;

    @ManyToOne
    private TeacherDashboard teacherDashboard;

    // Builder
    public QuestionStats() {
    }

    public QuestionStats(CourseExecution course_execution, TeacherDashboard teacher_dashboard) {
        setCourseExecution(course_execution);
        setTeacherDashboard(teacher_dashboard);
    }

    // GET
    public Integer getId() {
        return this.id;
    }

    public Integer getNumQuestionsAvailable() {
        return this.numQuestionsAvailable;
    }

    public Integer getNumQuestionsAnsweredUniq() {
        return this.numQuestionsAnsweredUniq;
    }

    public Double getAverageQuestionsAnsweredUniq() {
        return this.averageQuestionsAnsweredUniq;
    }

    public CourseExecution getCourseExecution() {
        return this.courseExecution;
    }

    public TeacherDashboard getTeacherDashboard() {
        return this.teacherDashboard;
    }

    // SET

    public void setNumQuestionsAvailable(Integer num) {
        this.numQuestionsAvailable = num;
    }

    public void accept(Visitor visitor) {
        // Only used for XML generation
    }

}

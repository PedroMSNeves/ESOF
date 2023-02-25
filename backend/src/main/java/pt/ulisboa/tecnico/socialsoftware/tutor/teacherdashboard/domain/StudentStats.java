package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;
import javax.persistence.*;
@Entity
public class StudentStats {
    @Id
    @OneToOne
    private CourseExecution courseExecution;

    @ManyToOne
    private TeacherDashboard teacherDashboard;

    public CourseExecution getCourseExecution(){ return courseExecution; }
    public TeacherDashboard getTeacherDashboard(){ return teacherDashboard; }
    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }
    public void setTeacherDashboard(TeacherDashboard teacherDashboard){
        this.teacherDashboard = teacherDashboard;
    }
}
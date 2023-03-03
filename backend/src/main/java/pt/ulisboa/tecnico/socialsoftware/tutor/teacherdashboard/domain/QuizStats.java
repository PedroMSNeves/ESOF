package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.GenerationType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

@Entity
public class QuizStats implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private int numQuizzes = 0;

    @OneToOne
    private CourseExecution courseExecution;

    public QuizStats() {
    }

    public QuizStats(CourseExecution courseExecution, TeacherDashboard teacherDashboard) {
        setCourseExecution(courseExecution);
    }

    public CourseExecution getCourseExecution(){ return courseExecution; }
    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public Integer getId() {
        return id;
    }

    public int getNumQuizzes() {
        return this.numQuizzes;
    }    
    public void setNumQuizzes(int value) {this.numQuizzes = value;}

    public void remove() {
        this.courseExecution = null;
    }
    public void accept(Visitor visitor) {
    }

    @Override
    public String toString() {
        return "QuizzesStats{" +
                "id=" + getId() +
                ", courseExecution=" + getCourseExecution() +
                ", numQuiz=" + getNumQuizzes() + '}';

    }
}

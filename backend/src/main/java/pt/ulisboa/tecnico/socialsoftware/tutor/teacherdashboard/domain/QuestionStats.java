package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.StudentDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
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

    private int numQuestionsAvailable = 0;
    private int numQuestionsAnsweredUniq = 0;
    private float averageQuestionsAnsweredUniq = 0;

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
    public int getId() {
        return this.id;
    }

    public int getNumQuestionsAvailable() {
        return this.numQuestionsAvailable;
    }

    public int getNumQuestionsAnsweredUniq() {
        return this.numQuestionsAnsweredUniq;
    }

    public float getAverageQuestionsAnsweredUniq() {
        return this.averageQuestionsAnsweredUniq;
    }

    public CourseExecution getCourseExecution() {
        return this.courseExecution;
    }

    public TeacherDashboard getTeacherDashboard() {
        return this.teacherDashboard;
    }

    // SET

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNumQuestionsAvailable(int numQuestionsavailable) {
        this.numQuestionsAvailable = numQuestionsavailable;
    }

    public void setNumQuestionsAnsweredUniq(int num) {
        this.numQuestionsAnsweredUniq = num;
    }

    public void setAverageQuestionsAnsweredUniq(float averageQuestionsAnsweredUniq) {
        this.averageQuestionsAnsweredUniq = averageQuestionsAnsweredUniq;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public void setTeacherDashboard(TeacherDashboard teacherDashboard) {
        this.teacherDashboard = teacherDashboard;
    }

    public void accept(Visitor visitor) {
        // Only used for XML generation
    }

    public void remove() {
        this.courseExecution = null;
        this.teacherDashboard = null;
        // ver se precisamos de adicionar mais ?
    }

    public void update() {
        this.setNumQuestionsAvailable(0);
        this.setNumQuestionsAnsweredUniq(0);
        this.setAverageQuestionsAnsweredUniq(0);
        int count = 0;
        float average = 0;// arredonda-se ou nao ????

        this.setNumQuestionsAvailable(this.courseExecution.getNumberOfQuestions());
        Set<Student> students = this.courseExecution.getStudents();

        for (Student stu : students) {
            Set<QuestionSubmission> questionSub = stu.getQuestionSubmissions();
            Set<Question> questions = new HashSet<>();
            for (QuestionSubmission qt : questionSub) {
                questions.add(qt.getQuestion());
            }
            count += questions.size();
        }
        this.setNumQuestionsAnsweredUniq(count);
        average =  count / (students.size());
        this.setAverageQuestionsAnsweredUniq(average);

    }

    @Override
    public String toString() {
        return "QuestionStats{" + "id = " + getId() + ",courseExecution = " + getCourseExecution() + ",teacherDashboard = "
                + getTeacherDashboard() + ",NumQuestionsAvailable = " + getNumQuestionsAvailable()
                + ",NumQuestionsAnsweredUniq = " + getNumQuestionsAnsweredUniq() + ",averageQuestionsAnsweredUniq = "
                + getAverageQuestionsAnsweredUniq() + "}";
    }

}

package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission;
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

    private int numQuestionsAvailable = 0;
    private int numQuestionsAnsweredUniq = 0;
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
    public int getId() {
        return this.id;
    }

    public int getNumQuestionsAvailable() {
        return this.numQuestionsAvailable;
    }

    public int getNumQuestionsAnsweredUniq() {
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

    public void setNumQuestionsAvailable(int numQuestionsavailable){
        this.numQuestionsAvailable = numQuestionsavailable;
    }

    public void setNumQuestionsAnsweredUniq(int num) {
        this.numQuestionsAnsweredUniq = num;
    }

    public void setAverageQuestionsAnsweredUniq(Double averageQuestionsAnsweredUniq) {
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

    public void remove(){
        this.courseExecution = null;
        this.teacherDashboard = null;
        //ver se precisamos de adicionar mais ?
    }

    public void update(){
        this.setNumQuestionsAvailable(0);
        this.setNumQuestionsAnsweredUniq(0);
        this.setAverageQuestionsAnsweredUniq(0.0);
        int count = 0;
        Double average = 0.0;//arredonda-se ou nao ????

        this.setNumQuestionsAvailable(this.teacherDashboard.getCourseExecution().getNumberOfQuestions());
        Set <Student> students = teacherDashboard.getCourseExecution().getStudents();

        for(Student stu : students){
            set<QuestionSubmission> questionSub = stu.getQuestionSubmissions();
            set<Question> questions = new Hashset<> ();
            for(QuestionSubmission qt :questionSub){
                questions.add(qt.getQuestion());
            }
            count += questions.size();
        }
        this.setNumQuestionsAnsweredUniq(count);
        average = count/(students.size());
        this.setAverageQuestionsAnsweredUniq(average);

    }

    @Override
    public String ToString(){
        return "Dashboard{" +
                "id = " + getId() +
                ",courseExecution = " + getCourseExecution() +
                ",teacherDashboard = " +  getTeacherDashboard() +
                ",NumQuestionsAvailable = " + getNumQuestionsAvailable() +
                ",NumQuestionsAnsweredUniq = " + getNumQuestionsAnsweredUniq() +
                ",averageQuestionsAnsweredUniq = " + getAverageQuestionsAnsweredUniq() + "}";
    }


}
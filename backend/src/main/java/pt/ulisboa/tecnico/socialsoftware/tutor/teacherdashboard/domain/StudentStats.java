package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;


import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.StudentDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import java.util.Optional;
import javax.persistence.*;
import java.util.stream.Collectors;
import java.util.*;
@Entity
public class StudentStats implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int numStudent =0;
    private int numMore75CorrectQuestions =0;
    private int numAtLeast3Quizzes =0;

    @OneToOne
    private CourseExecution courseExecution;

    @ManyToOne
    private TeacherDashboard teacherDashboard;

    public StudentStats() {
    }

    public StudentStats(CourseExecution courseExecution, TeacherDashboard teacherDashboard) {
        setCourseExecution(courseExecution);
        setTeacherDashboard(teacherDashboard);
    }

    public CourseExecution getCourseExecution(){ return courseExecution; }
    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }
    public TeacherDashboard getTeacherDashboard(){ return teacherDashboard; }
    public void setTeacherDashboard(TeacherDashboard teacherDashboard){
        this.teacherDashboard = teacherDashboard;
        this.teacherDashboard.addStudentStats(this);
    }

    public Integer getId() {
        return this.id;
    }

    public int getNumStudent(){
        return this.numStudent;
    }
    public void setNumStudent(int numstudents){
        this.numStudent= numstudents;
    }
    public void addNumStudent(){
        this.numStudent++;
    }
    public int getNumMore75CorrectQuestions(){
        return this.numMore75CorrectQuestions;
    }
    public void setNumMore75CorrectQuestions(int value){
        this.numMore75CorrectQuestions= value;
    }
    public void addNumMore75CorrectQuestions(){
        this.numMore75CorrectQuestions++;
    }
    public int getNumAtLeast3Quizzes(){
        return this.numAtLeast3Quizzes;
    }
    public void setNumAtLeast3Quizzes(int value){
        this.numAtLeast3Quizzes= value;
    }
    public void addNumAtLeast3Quizzes(){
        this.numAtLeast3Quizzes++;
    }
    public void accept(Visitor visitor) {
        // Only used for XML generation
    }
    public void remove() {
        getTeacherDashboard().getStudentStats().remove(this);
        this.courseExecution = null;
        this.teacherDashboard = null;
    }
    public void update() {
        this.setNumStudent(0);
        this.setNumAtLeast3Quizzes(0);
        this.setNumMore75CorrectQuestions(0);
        Set<Student> students = courseExecution.getStudents();

        for (Student st : students) {
            int totalq=0;
            int trueq=0;
            this.addNumStudent();
            Set<QuizAnswer> quizAnswer= st.getQuizAnswers();
            for(QuizAnswer qa :quizAnswer){
                totalq+=qa.getQuiz().getQuizQuestionsNumber();
                trueq+=qa.getNumberOfCorrectAnswers();
            }
            if(totalq>0 && (trueq*100/totalq)>75) {
                this.addNumMore75CorrectQuestions();
            }
            //System.out.println(totalq + "+"+ trueq);
            if(quizAnswer.stream().distinct().count() >= 3) {
                this.addNumAtLeast3Quizzes();
            }
        }
    }
    @Override
    public String toString() {
        return "Dashboard{" +
                "id=" + getId() +
                ", courseExecution=" + getCourseExecution() +
                ", teacherDashboard=" + getTeacherDashboard() +
                ", numStudent=" + getNumStudent() +
                ", numMore75CorrectQuestions=" + getNumMore75CorrectQuestions() +
                ", numAtLeast3Quizzes=" + getNumAtLeast3Quizzes() + '}';
    }
}

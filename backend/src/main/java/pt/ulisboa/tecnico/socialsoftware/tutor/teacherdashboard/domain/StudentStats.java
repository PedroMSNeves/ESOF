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
//import java.io.Serializable;

@Entity
public class StudentStats implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer numStudent =0;
    private Integer numMore75CorrectQuestions =0;
    private Integer numAtLeast3Quizzes =0;

    @OneToOne
    private CourseExecution courseExecution;

    @ManyToOne
    private TeacherDashboard teacherDashboard;

    public StudentStats() {
    }

    public StudentStats(CourseExecution courseExecution, TeacherDashboard teacherDashboard) {
        //verificar se o teacher da a disciplina?
        //meter teacherDashboard?
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
    }

    public Integer getId() {
        return id;
    }

    public Integer getNumStudent(){
        return this.numStudent;
    }
    public void setNumStudent(int numstudents){
        this.numStudent= numstudents;
    }
    public Integer getNumMore75CorrectQuestions(){
        return this.numMore75CorrectQuestions;
    }
    public void setNumMore75CorrectQuestions(int value){
        this.numMore75CorrectQuestions= value;
    }
    public void addNumMore75CorrectQuestions(){
        this.numMore75CorrectQuestions++;
    }
    public Integer getNumAtLeast3Quizzes(){
        return this.numAtLeast3Quizzes;
    }
    public void setNumAtLeast3Quizzes(int value){
        this.numAtLeast3Quizzes= value;

    }
    public void addNumAtLeast3Quizzes(){
        this.numAtLeast3Quizzes++;
    }

    public void remove() {
        //this.courseExecution.getWeeklyScores().remove(this);
        //this.teacherDashboard.getWeeklyScores().remove(this);                         //PERGUNTAR
        this.courseExecution = null;
        this.teacherDashboard = null;
    }
    public void update() {
        this.setNumStudent(0);
        this.setNumAtLeast3Quizzes(0);
        this.setNumMore75CorrectQuestions(0);

        Set<Student> students = teacherDashboard.getCourseExecution().getStudents(); //podemos filtrar assim?
        this.setNumStudent(teacherDashboard.getCourseExecution().getNumberOfActiveStudents()); // or courseExecution.getNumberOfActiveStudents();
        //queremos so os ativos?

        for (Student st : students) {
            StudentDashboard stdb= st.getDashboards().stream().filter(dash -> dash.getCourseExecution().getId() == teacherDashboard.getCourseExecution().getId()).findFirst().get();
            //porque so vai ter um dashboard por disciplina, perguntar se pode ser assim

            if((stdb.getNumberOfTeacherQuizzes() + stdb.getNumberOfStudentQuizzes() + stdb.getNumberOfInClassQuizzes())>=3) {this.addNumAtLeast3Quizzes();}

            if(((stdb.getNumberOfCorrectTeacherAnswers() + stdb.getNumberOfCorrectStudentAnswers() + stdb.getNumberOfCorrectInClassAnswers())*100/
                    (stdb.getNumberOfTeacherAnswers() + stdb.getNumberOfStudentAnswers() + stdb.getNumberOfInClassAnswers()))
                    >75) {this.addNumMore75CorrectQuestions();}
        }//perguntar tambem

    }

    public void accept(Visitor visitor) {
        // Only used for XML generation
    }
    @Override
    public String toString() {
        return "Dashboard{" +
                "id=" + getId() +
                "courseExecution=" + getCourseExecution() +
                ", teacherDashboard=" + getTeacherDashboard() + '}';
    }
}
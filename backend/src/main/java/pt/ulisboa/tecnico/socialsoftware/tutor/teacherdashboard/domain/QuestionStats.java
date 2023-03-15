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
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question.Status;
import javax.persistence.*;
import java.util.stream.Collectors;
import java.util.*;

@Entity
public class QuestionStats implements DomainEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int numQuestionsAvailable = 0;

    private int numQuestionsAnswersuniq = 0;

    private float averageQuestionsAnsweredUniq = 0;

    @OneToOne
    private CourseExecution courseExecution;

    @ManyToOne
    private TeacherDashboard teacherDashboard;


    // Builder
    public QuestionStats() {
    }

    public QuestionStats(CourseExecution course_execution, TeacherDashboard teacher_dashboard) {
        teacher_dashboard.addQuestionStats(this);
        setCourseExecution(course_execution);
        setTeacherDashboard(teacher_dashboard);
    }

    // GET
    public Integer getId() {
        return this.id;
    }

    public int getNumQuestionsAvailable() {
        return this.numQuestionsAvailable;
    }

    public CourseExecution getCourseExecution() {
        return this.courseExecution;
    }

    public TeacherDashboard getTeacherDashboard() {
        return this.teacherDashboard;
    }

    public int getNumQuestionsAnsweredUniq(){
        return this.numQuestionsAnswersuniq;
    }

    public float getAverageQuestionsAnsweredUniq() {
        return this.averageQuestionsAnsweredUniq;
    }

    //SET
    public void setNumQuestionsAvailable(int numQuestionsavailable) {
        this.numQuestionsAvailable = numQuestionsavailable;
    }

    public void setNumQuestionsAnsweredUniq(int numQuestionsAnswersuniq) {
        this.numQuestionsAnswersuniq = numQuestionsAnswersuniq;
    }

    public void setAverageQuestionsAnsweredUniq(float averageQuestionsAnsweredUniq) {
        this.averageQuestionsAnsweredUniq = averageQuestionsAnsweredUniq;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public void setTeacherDashboard(TeacherDashboard teacherDashboard) {
        this.teacherDashboard = teacherDashboard;
        this.teacherDashboard.addQuestionStats(this);
    }

    public void accept(Visitor visitor) {
        // Only used for XML generation
    }

    public void remove() {
    	this.getTeacherDashboard().getQuestionStats().remove(this);
        this.courseExecution = null;
        this.teacherDashboard = null;
    }

    public void update() {
        // number of available questions
        this.numQuestionsAvailable = (int) courseExecution.getQuizzes().stream()
            .flatMap(q -> q.getQuizQuestions().stream())
            .map(QuizQuestion::getQuestion)
            .filter(q -> q.getStatus() == Status.AVAILABLE)
            .distinct()
            .count();

        // number of answered questions at least once
        this.numQuestionsAnswersuniq = (int) courseExecution.getQuizzes().stream()
                .flatMap(q -> q.getQuizAnswers() .stream()
                    .flatMap(qa -> qa.getQuestionAnswers().stream()
                        .map(QuestionAnswer::getQuestion)))
            .distinct()
            .count();

        // number of students
        int students = courseExecution.getStudents().size();

        long uniqueAllStudents = courseExecution.getStudents().stream().mapToLong(student ->
            student.getQuizAnswers().stream().flatMap(
                qa -> qa.getQuestionAnswers().stream().map(QuestionAnswer::getQuestion)
            ).distinct().count()).sum();

        // average
        this.averageQuestionsAnsweredUniq = students > 0 ? (float) uniqueAllStudents / students : 0.0f;
    }

    @Override
    public String toString() {
        return "QuestionStats{" + "id = " + getId() + ",courseExecution = " + getCourseExecution() + ",teacherDashboard = "
                + getTeacherDashboard() + ",NumQuestionsAvailable = " + getNumQuestionsAvailable()
                + ",NumQuestionsAnsweredUniq = " + getNumQuestionsAnsweredUniq() + "}";
    }


}

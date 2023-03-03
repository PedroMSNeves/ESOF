package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;


import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats;
import java.util.HashSet;
import java.util.Set;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;


@Entity
public class TeacherDashboard implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private CourseExecution courseExecution;

    @ManyToOne
    private Teacher teacher;

    @OneToMany
    private final Set<QuestionStats> questionStats = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "teacherDashboard", orphanRemoval = true)
    private final Set<StudentStats> studentStats = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "teacherDashboard", orphanRemoval = true)
    private final Set<QuizStats> quizStats = new HashSet<>();

    public TeacherDashboard() {
    }

    public TeacherDashboard(CourseExecution courseExecution, Teacher teacher) {
        setCourseExecution(courseExecution);
        setTeacher(teacher);
    }

    public Set<QuestionStats> getQuestionStats(){
        return this.questionStats;
    }
    
    public QuestionStats getCourseExecutionQuestionStats(CourseExecution ce) {
    	return this.getQuestionStats().stream()
    			.filter(ss -> ss.getCourseExecution() == ce)
    			.findAny().orElse(null);
    }

    public void addQuestionStats(QuestionStats qst){
        if (questionStats.stream().anyMatch(questionStat1 -> questionStat1.getId() == qst.getId()) ) {
            throw new TutorException(ErrorMessage.QUESTIONS_STATS_ALREADY_STORED);
        }

        this.questionStats.add(qst);
    }

    public void remove() {
        teacher.getDashboards().remove(this);
        teacher = null;
    }

    public Integer getId() {
        return id;
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
        this.teacher.addDashboard(this);
    }
    public Set<StudentStats> getStudentStats(){ return this.studentStats; }
    public StudentStats getCourseExecutionStudentStats(CourseExecution ce) {
        return getStudentStats().stream()
                .filter(ss -> ss.getCourseExecution() == ce)
                .findAny()
                .orElse(null);
    }
    public void addStudentStats(StudentStats studentStat) {
        if (studentStats.stream().anyMatch(studentStat1 -> studentStat1.getId() == studentStat.getId()) ) {
            throw new TutorException(ErrorMessage.STUDENT_STATS_ALREADY_CREATED);
        }
        studentStats.add(studentStat);
    }
    public void update() {
        for(StudentStats st: getStudentStats()) {
            st.update();
        }
        for(QuizStats quizStats: getQuizStats()) {
            quizStats.update();
        }
        for(QuestionStats qst: getQuestionStats()) {
            qst.update();
        }
    }

    public Set<QuizStats> getQuizStats() {return this.quizStats;}

    public void addQuizStats(QuizStats value) {
        if(quizStats.stream().anyMatch(quizStat1 -> quizStat1.getId() == value.getId())) {
            throw new TutorException(ErrorMessage.QUIZ_STATS_ALREADY_CREATED);
        }
        quizStats.add(value);
    }
    public QuizStats getCourseExecutionQuizStats(CourseExecution courseExecution) {
        return getQuizStats().stream()
                .filter(ss -> ss.getCourseExecution() == courseExecution)
                .findAny()
                .orElse(null);
    }

    public void accept(Visitor visitor) {
        // Only used for XML generation
    }

    @Override
    public String toString() {
        return "Dashboard{" +
                "id=" + id +
                ", courseExecution=" + courseExecution +
                ", teacher=" + teacher +
                '}';
    }

}

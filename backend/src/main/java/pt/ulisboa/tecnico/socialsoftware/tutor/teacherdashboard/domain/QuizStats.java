package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.GenerationType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import java.util.Set;
import java.util.HashSet;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;

@Entity
public class QuizStats implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private int numQuizzes = 0;
    private int uniqueQuizzesSolved = 0;
    private float averageQuizzesSolved = 0;

    @OneToOne
    private CourseExecution courseExecution;

    @ManyToOne
    private TeacherDashboard teacherDashboard;

    public QuizStats() {
    }

    public QuizStats(CourseExecution courseExecution, TeacherDashboard teacherDashboard) {
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
        this.teacherDashboard.addQuizStats(this);
    }

    public Integer getId() {
        return id;
    }

    public int getNumQuizzes() {
        return this.numQuizzes;
    }    
    public void setNumQuizzes(int value) {this.numQuizzes = value;}

    public int getUniqueQuizzesSolved() {
        return this.uniqueQuizzesSolved;
    }
    public void setUniqueQuizzesSolved(int value) {this.uniqueQuizzesSolved = value;}
    
    public float getAverageQuizzesSolved() {
        return this.averageQuizzesSolved;
    }
    public void setAverageQuizzesSolved(float value) {this.averageQuizzesSolved = value;}

    public void remove() {
        this.teacherDashboard.getQuizStats().remove(this);
        this.courseExecution = null;
        this.teacherDashboard = null;
    }

    public void update() {
        Set<Student> students = this.courseExecution.getStudents();
        this.numQuizzes = this.courseExecution.getNumberOfQuizzes(); 
        //here we use the student class to acess the unique quizes that each student resolves
        
        if (students.size() == 0) {
            this.averageQuizzesSolved = 0;
            this.uniqueQuizzesSolved = 0;
            return;
        }
        Set<Quiz> quizCourse = this.courseExecution.getQuizzes();
        Set<Quiz> uniqueQuizzesAll = new HashSet<>();
        for(Student st : students) {
            Set<Quiz> uniqueQuizesStudent = new HashSet<>();
            Set<QuizAnswer> quizAnswers = st.getQuizAnswers();
            for(QuizAnswer quizAnswer : quizAnswers) {
                Quiz eachQuiz = quizAnswer.getQuiz();
                if(quizCourse.stream().anyMatch(quiz -> quiz.getId() == eachQuiz.getId())) {    
                    uniqueQuizesStudent.add(eachQuiz);
                    uniqueQuizzesAll.add(eachQuiz);
                }
            }
            this.averageQuizzesSolved = this.averageQuizzesSolved + uniqueQuizesStudent.size(); 
        }
        this.averageQuizzesSolved = this.averageQuizzesSolved / (students.size());
        this.uniqueQuizzesSolved = uniqueQuizzesAll.size();
    }

    public void accept(Visitor visitor) {
    }

    @Override
    public String toString() {
        return "QuizzesStats{" +
                "id=" + getId() +
                ", courseExecution=" + getCourseExecution() +
                ", teacherDashboard=" + getTeacherDashboard() +
                ", numQuiz=" + getNumQuizzes() +
                ", numUniqueQuizzes=" + getUniqueQuizzesSolved() +
                ", numAverageQuizzesSolved=" + getAverageQuizzesSolved() + '}';

    }
}


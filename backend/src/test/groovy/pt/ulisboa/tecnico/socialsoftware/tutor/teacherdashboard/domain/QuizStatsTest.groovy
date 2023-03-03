package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.StudentDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course

import spock.lang.Unroll

@DataJpaTest
class QuizStatsTest extends SpockTest {
    def teacherDashboard
    def teacher

    def setup() {
        createExternalCourseAndExecution()
        teacher = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher)
        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)
    }

    def newQuizStats(td, ce) {
        def quizStats = new QuizStats(ce,td)
        quizStatsRepository.save(quizStats)
        td.addQuizStats(quizStats)
        return quizStats
    }

    def newCourseExecution(num) {
        def newCourse = new Course(num, Course.Type.TECNICO)
        courseRepository.save(newCourse)
        def newCE  = new CourseExecution(newCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(newCE)
        return newCE
    }

    def newStudent(courseExecution, username){
        def student = new Student(USER_1_NAME, username, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student.addCourse(courseExecution)
        userRepository.save(student)
        def studentDashboard = new StudentDashboard(courseExecution, student)
        studentDashboardRepository.save(studentDashboard)
        return student
    }

    def newQuiz(courseExecution) {
        def quiz = new Quiz()
        quiz.setCourseExecution(courseExecution)
        quizRepository.save(quiz)
        return quiz
    }

    def createQuiz(type = Quiz.QuizType.PROPOSED.toString()) {
        def quiz = new Quiz()
        quiz.setTitle("Quiz Title")
        quiz.setType(type)
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setCreationDate(DateHandler.now())
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)
        return quiz
    }

    def createQuizQuestion(quiz, question) {
        def quizQuestion = new QuizQuestion(quiz, question, 0)
        quizQuestionRepository.save(quizQuestion)
        return quizQuestion
    }

    def createQuizAnswer(st,qu) {
        def quizAnswer = new QuizAnswer(st, qu)
        quizAnswerRepository.save(quizAnswer)
        return quizAnswer
    }

    @Unroll
    def "create an empty QuizStats"() {
        when: "a quizStats is created"
        newQuizStats(teacherDashboard, externalCourseExecution)
        then: "compare if is created correctly"
        quizStatsRepository.count() == 1L
        def result = quizStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacherDashboard().getId() == teacherDashboard.getId()

        and: "the teacherDashboard has a reference for the quizStats"
        teacherDashboard.getQuizStats().size() == 1
        teacherDashboard.getQuizStats().contains(result)
    }

    @Unroll
    def "create an empty QuizStats and remove it"() {
        when: "a quizStats is created"
        newQuizStats(teacherDashboard, externalCourseExecution)
        then: "an empty quizStats is compared and then deleted"
        quizStatsRepository.count() == 1L
        def result = quizStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacherDashboard().getId() == teacherDashboard.getId()
        teacherDashboard.getQuizStats().size() == 1
        result.remove()
        and: "the teacherDashboard has no reference for the quizStats"
        teacherDashboard.getQuizStats().size() == 0
    }

    @Unroll
    def "create an empty QuizStats and alter it"() {
        when: "a quizStats is created"
        def quizStats = newQuizStats(teacherDashboard, externalCourseExecution)
        quizStats.setNumQuizzes(4)
        quizStats.setUniqueQuizzesSolved(3)
        quizStats.setAverageQuizzesSolved(0.5) 
        then: "compare if is created correctly"
        quizStatsRepository.count() == 1L
        def result = quizStatsRepository.findAll().get(0)
        result.getNumQuizzes() == 4
        result.getUniqueQuizzesSolved() == 3
        result.getAverageQuizzesSolved() == 0.5
    }

    @Unroll
    def "create an empty QuizStats and get it from teacherdashboard with the course"() {
        when: "a quizStats is created"
        newQuizStats(teacherDashboard,externalCourseExecution)
        def newCE = newCourseExecution("123")

        then: "an empty quizStats is created and get from teacherdashboard with the course"
        quizStatsRepository.count() == 1L
        def result = quizStatsRepository.findAll().get(0)
        teacherDashboard.getCourseExecutionQuizStats(externalCourseExecution)==result
        teacherDashboard.getCourseExecutionQuizStats(newCE)==null
    }

    @Unroll
    def "create an empty QuizStats and add it 2 times to teacherDashboard"() {
        when: "a quizStats is created"
        newQuizStats(teacherDashboard,externalCourseExecution)

        then: "try to add the quizStats again"
        quizStatsRepository.count() == 1L
        def result = quizStatsRepository.findAll().get(0)
        def c=0;

        try {
            teacherDashboard.addQuizStats(result)
        }
        catch(Exception QUIZ_STATS_ALREADY_CREATED) {
            c=1
        }
        c==1
    }

    @Unroll
    def "create 2 empty QuizStats and add them to teacherDashboard"() {
        when: "a quizStats is created and add to teacherDashboard"
        def quizStats1=newQuizStats(teacherDashboard,externalCourseExecution)
        def newCE = newCourseExecution("321")
        def quizStats2=newQuizStats(teacherDashboard,newCE)

        then: "see if added"
        teacherDashboard.getCourseExecutionQuizStats(externalCourseExecution)==quizStats1
        teacherDashboard.getCourseExecutionQuizStats(newCE)==quizStats2
    }

    @Unroll
    def "create empty quizStats and use toString"() {
        when: "a quizStats is created"
        def newTeacher = new Teacher(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        userRepository.save(newTeacher)
        def td = new TeacherDashboard(externalCourseExecution, newTeacher)
        teacherDashboardRepository.save(td)
        newQuizStats(td,externalCourseExecution)

        then: "compare toString"
        quizStatsRepository.count() == 1L
        def result = quizStatsRepository.findAll().get(0)
        result.toString()==td.getCourseExecutionQuizStats(externalCourseExecution).toString()
    }


    @Unroll
    def "create an empty QuizStats and updated with empty course"() {
        when: "a quizStats is created"
        newQuizStats(teacherDashboard, externalCourseExecution)
        then: "an empty quizStats is created and updated with empty course"
        quizStatsRepository.count() == 1L
        def result = quizStatsRepository.findAll().get(0)
        result.update()
        result.getNumQuizzes() == 0
        result.getUniqueQuizzesSolved() == 0
        result.getAverageQuizzesSolved() == 0
    }

    @Unroll
    def "create an empty QuizStats and updated with course that has 1 empty quiz"() {
        when: "a quizStats is created"
        newQuizStats(teacherDashboard, externalCourseExecution) 
        def quiz = newQuiz(externalCourseExecution)
        then: "compare and update the course that has 1 empty quiz"
        quizStatsRepository.count() == 1L
        def result = quizStatsRepository.findAll().get(0)
        result.update()
        result.getNumQuizzes() == 1
        result.getUniqueQuizzesSolved() == 0
        result.getAverageQuizzesSolved() == 0
    }

    @Unroll
    def "create a QuizStats that has been solved by a student"() {
        when: "a quizStats is created"
        def quizStats = newQuizStats(teacherDashboard, externalCourseExecution)
        def quiz = newQuiz(externalCourseExecution)
        def student = newStudent(externalCourseExecution,"toni")
        def quizAnswer = createQuizAnswer(student, quiz)
        
        then: "compare and update the course with one quiz"
        quizStatsRepository.count() == 1L
        def result = quizStatsRepository.findAll().get(0)
        result.update()
        result.getNumQuizzes() == 1
        result.getUniqueQuizzesSolved() == 1
        result.getAverageQuizzesSolved() == 1
    }

    @Unroll
    def "create a QuizStats that has been solved by a student and one empty"() {
        when: "the quizStats are created"
        def quizStats = newQuizStats(teacherDashboard, externalCourseExecution)
        def quiz = newQuiz(externalCourseExecution)
        def quiz2 = newQuiz(externalCourseExecution)
        def student = newStudent(externalCourseExecution,"toni")
        def quizAnswer = createQuizAnswer(student, quiz)
        
        then: "compare and update the course with one quiz"
        quizStatsRepository.count() == 1L
        def result = quizStatsRepository.findAll().get(0)
        result.update()
        result.getNumQuizzes() == 2
        result.getUniqueQuizzesSolved() == 1
        result.getAverageQuizzesSolved() == 1
    }

    @Unroll
    def "create a quiz and attribute it to two different students"() {
        when: "the quizStats are created"
        newQuizStats(teacherDashboard, externalCourseExecution)
        def quiz = newQuiz(externalCourseExecution)
        def student = newStudent(externalCourseExecution,"toni")
        def student2 = newStudent(externalCourseExecution,"inot")
        def quizAnswer = createQuizAnswer(student, quiz)
        def quizAnser2 = createQuizAnswer(student2, quiz)
        
        then: "compare and update the course with one quiz"
        quizStatsRepository.count() == 1L
        def result = quizStatsRepository.findAll().get(0)
        result.update()
        result.getNumQuizzes() == 1
        result.getUniqueQuizzesSolved() == 1
        result.getAverageQuizzesSolved() == 1
    }

    @Unroll
    def "create two quizzes for two students"() {
        when: "the quizStats are created"
        newQuizStats(teacherDashboard, externalCourseExecution)
        def quiz = newQuiz(externalCourseExecution)
        def quiz2 = newQuiz(externalCourseExecution)
        def student = newStudent(externalCourseExecution,"toni")
        def student2 = newStudent(externalCourseExecution,"inot")
        def quizAnswer = createQuizAnswer(student, quiz)
        def quizAnser2 = createQuizAnswer(student2, quiz2)
        
        then: "compare and update the course with one quiz"
        quizStatsRepository.count() == 1L
        def result = quizStatsRepository.findAll().get(0)
        result.update()
        result.getNumQuizzes() == 2
        result.getUniqueQuizzesSolved() == 2
        result.getAverageQuizzesSolved() == 1
    }
	
	@Unroll
	def "create two quizzes and add them to one student"() {
		when: "the quizStats are created"
		newQuizStats(teacherDashboard, externalCourseExecution)
		def quiz = newQuiz(externalCourseExecution)
		def quiz2 = newQuiz(externalCourseExecution)
		def student = newStudent(externalCourseExecution,"toni")
		def quizAnswer = createQuizAnswer(student, quiz)
		def quizAnswer2 = createQuizAnswer(student, quiz2)
		
		then: "compare and update the course"
		quizStatsRepository.count() == 1L
		def result = quizStatsRepository.findAll().get(0)
		result.update()
		result.getNumQuizzes() == 2
		result.getUniqueQuizzesSolved() == 2
		result.getAverageQuizzesSolved() == 2
	}

    @Unroll
    def "create two quizzes for a student and another student with no quizzes"() {
        when: "the quizStats are created"
		newQuizStats(teacherDashboard, externalCourseExecution)
		def quiz = newQuiz(externalCourseExecution)
		def quiz2 = newQuiz(externalCourseExecution)
		def student = newStudent(externalCourseExecution,"toni")
        def student2 = newStudent(externalCourseExecution,"inot")
		def quizAnswer = createQuizAnswer(student, quiz)
		def quizAnswer2 = createQuizAnswer(student, quiz2)
		
		then: "compare and update the course"
		quizStatsRepository.count() == 1L
		def result = quizStatsRepository.findAll().get(0)
		result.update()
		result.getNumQuizzes() == 2
		result.getUniqueQuizzesSolved() == 2
		result.getAverageQuizzesSolved() == 1
    }

    @Unroll
    def "create a quiz for a student not in the course and one in the course"() {
        when: "the quizStats is created"
        newQuizStats(teacherDashboard, externalCourseExecution)
		def quiz = newQuiz(externalCourseExecution)
        def ce = newCourseExecution("456")
		def quiz2 = newQuiz(ce)
		def student = newStudent(externalCourseExecution,"toni")
		def quizAnswer = createQuizAnswer(student, quiz)
		def quizAnswer2 = createQuizAnswer(student, quiz2)
		
		then: "compare and update the course"
		quizStatsRepository.count() == 1L
		def result = quizStatsRepository.findAll().get(0)
		result.update()
		result.getNumQuizzes() == 1
		result.getUniqueQuizzesSolved() == 1
		result.getAverageQuizzesSolved() == 1
    }
        
    @Unroll
    def "create a quizStats and update it using teacherDashboard"() {
        when: "a quizStats is created"
        newQuizStats(teacherDashboard, externalCourseExecution) 
        def quiz = newQuiz(externalCourseExecution)
        then: "compare and update the course that has 1 empty quiz"
        quizStatsRepository.count() == 1L
        def result = quizStatsRepository.findAll().get(0)
        teacherDashboard.update()
        result.getNumQuizzes() == 1
        result.getUniqueQuizzesSolved() == 0
        result.getAverageQuizzesSolved() == 0
    }
    


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}

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

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}


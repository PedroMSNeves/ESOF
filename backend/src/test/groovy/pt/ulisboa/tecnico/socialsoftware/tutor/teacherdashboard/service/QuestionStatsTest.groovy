package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.StudentDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course

import spock.lang.Unroll

@DataJpaTest
class QuestionStatsTest extends SpockTest {
    def teacherDashboard
    def teacher

    def setup() {
        createExternalCourseAndExecution()
        teacher = new Teacher(USER_1_NAME, false)
        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)
    }


    //mudar o spocktest.groovy
    @Unroll
    def "create an empty QuestionStats"() {
        when: "a questionStats is created"
        def questionStats = new QuestionStats(externalCourseExecution, teacherDashboard)
        //QuestionStatsRepository.save(questionStats)
        teacherDashboard.addQuestionStats(questionStats)
        then: "an empty dashboard is created"
        //QuestionStatsRepository.count() == 1L
        def result = questionStats//studentStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacherDashboard().getId() == teacherDashboard.getId()

        and: "the teacherDashboard has a reference for the QuestionStats"
        teacherDashboard.getQuestionStats().size() == 1
        teacherDashboard.getQuestionStats().contains(result)
    }

    @Unroll
    def "create an empty QuestionStats and remove it"() {
        when: "a QuestionStats is created"
        def questionStats = new QuestionStats(externalCourseExecution, teacherDashboard)
        //QuestionStatsRepository.save(questionStats) perguntar
        teacherDashboard.addQuestionStats(questionStats)
        then: "an empty questionStats is created and deleted"
        //QestionStatsRepository.count() == 1L
        def result = questionStats//studentStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacherDashboard().getId() == teacherDashboard.getId()
        teacherDashboard.remove()
        and: "the teacherDashboard has no reference for the questionStats"
        teacherDashboard.getStudentStats().size() == 0
    }

    @Unroll
    def "create an empty QuestionStats and updated with empty course"() {
        when: "a questionStats is created"
        def questionStats = new QuestionStats(externalCourseExecution, teacherDashboard)
        //QuestionStatsRepository.save(questionStats) perguntar
        teacherDashboard.addQuestionStats(questionStats)
        then: "an empty studentStats is created and updated with empty course"
        //QuestionStatsRepository.count() == 1L
        def result = questionStats//studentStatsRepository.findAll().get(0)
        questionStats.update()
        result.getNumQuestionsAvailable() == 0
        result.getNumQuestionsAnsweredUniq() == 0
        result.getAverageQuestionsAnsweredUniq() == 0
    }

    @Unroll
    def "create an empty QuestionStats and updated with course that as 1 empty question"() {
        when: "a questionStats is created"
        def questionStats = new QuestionStats(externalCourseExecution, teacherDashboard)
        //questionStatsRepository.save(questionStats) perguntar
        teacherDashboard.addQuestionStats(questionStats)
        def question = new Question(course,questionDto)
        student.addCourse(externalCourseExecution)
        then: "an empty questionStats is created and updated course that as 1 empty question"
        //QuestionStatsRepository.count() == 1L
        def result = questionStats//QuestionStatsRepository.findAll().get(0)

        questionStats.update()
        result.getNumQuestionsAvailable() == 1
        result.getNumQuestionsAnsweredUniq() == 0
        result.getAverageQuestionsAnsweredUniq() == 0
    }

    @Unroll
    def "create an empty QuestionStats and updated with course that as 1 question but question does not have it"() {
        when: "a QuestionStats is created and a studentDashboard"
        def questionStats = new QuestionStats(externalCourseExecution, teacherDashboard)
        //QuestionStatsRepository.save(studentStats)
        teacherDashboard.addSQuestionStats(questionStats)
        def qst = new Question(externalCourseExecution, student)
        qst.addCourse(externalCourseExecution)
        def newCourse = new Course(COURSE_1_NAME, Course.Type.TECNICO)
        def newCourseExecution = new CourseExecution(newCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        then: "an empty questionStats is created and updated course that as 1 question but question does not have it"
        def result = questionStats//QuestionStatsRepository.findAll().get(0)

        questionStats.update()
        result.getNumQuestionsAvailable() == 0
        result.getNumQuestionsAnsweredUniq() == 0
        result.getAverageQuestionsAnsweredUniq() == 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}


}
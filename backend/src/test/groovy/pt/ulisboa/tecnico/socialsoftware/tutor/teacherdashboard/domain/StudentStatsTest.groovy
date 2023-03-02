package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.StudentDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

import spock.lang.Unroll

@DataJpaTest
class StudentStatsTest extends SpockTest {
    def teacherDashboard
    def teacher

    def setup() {
        createExternalCourseAndExecution()
        teacher = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher)
        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)
    }

    def newstudent(courseExecution,username){
        def student = new Student(USER_1_NAME, username, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student.addCourse(courseExecution)
        userRepository.save(student)
        def studentDashboard = new StudentDashboard(courseExecution, student)
        studentDashboardRepository.save(studentDashboard)
        return student
    }

    def newStudentStats(td,ce){
        def studentStats = new StudentStats(ce, td)
        studentStatsRepository.save(studentStats)
        td.addStudentStats(studentStats)
        return studentStats
    }
    def newCourseExecution(name)
    {
        def newCourse = new Course(name, Course.Type.TECNICO)
        courseRepository.save(newCourse)
        def newCE  = new CourseExecution(newCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(newCE)
        return newCE
    }

    @Unroll
    def "create an empty StudentStats"() {
        when: "a studentStats is created"
        newStudentStats(teacherDashboard,externalCourseExecution)

        then: "compare if created correctly"
        studentStatsRepository.count() == 1L
        def result = studentStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacherDashboard().getId() == teacherDashboard.getId()

        and: "the teacherDashboard has a reference for the studentStats"
        teacherDashboard.getStudentStats().size() == 1
        teacherDashboard.getStudentStats().contains(result)
    }

    @Unroll
    def "create an empty StudentStats and remove it"() {
        when: "a studentStats is created"
        newStudentStats(teacherDashboard,externalCourseExecution)

        then: "an empty studentStats is created and deleted"
        studentStatsRepository.count() == 1L
        def result = studentStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacherDashboard().getId() == teacherDashboard.getId()
        teacherDashboard.getStudentStats().size() == 1
        result.remove()

        and: "the teacherDashboard has no reference for the studentStats"
        teacherDashboard.getStudentStats().size() == 0
    }

    @Unroll
    def "create an empty StudentStats and updated with empty course"() {
        when: "a studentStats is created"
        newStudentStats(teacherDashboard,externalCourseExecution)

        then: "an empty studentStats is created and updated with empty course"
        studentStatsRepository.count() == 1L
        def result = studentStatsRepository.findAll().get(0)
        result.update()
        result.getNumStudent() == 0
        result.getNumMore75CorrectQuestions() == 0
        result.getNumAtLeast3Quizzes() == 0
    }


    @Unroll
    def "create an empty StudentStats and updated with course that as 1 empty student"() {
        when: "a studentStats and student are created"
        newstudent(externalCourseExecution,"dadsad")
        newStudentStats(teacherDashboard,externalCourseExecution)


        then: "an empty studentStats is created and updated with course that as 1 empty student"
        studentStatsRepository.count() == 1L
        def result = studentStatsRepository.findAll().get(0)
        result.update()
        result.getNumStudent() == 1
        result.getNumMore75CorrectQuestions() == 0
        result.getNumAtLeast3Quizzes() == 0
    }

    @Unroll
    def "create an empty StudentStats and updated with course that as 1 student but student does not have it"() {
        when: "a studentStats is created and a studentDashboard"
        newStudentStats(teacherDashboard,externalCourseExecution)
        def student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
        def newCE = newCourseExecution("123")
        def studentdashboard = new StudentDashboard(newCE, student)
        studentDashboardRepository.save(studentdashboard)

        then: "an empty studentStats is created and updated course that as 1 student but student does not have it"
        studentStatsRepository.count() == 1L
        def result = studentStatsRepository.findAll().get(0)

        result.update()
        result.getNumStudent() == 0
        result.getNumMore75CorrectQuestions() == 0
        result.getNumAtLeast3Quizzes() == 0
    }



    @Unroll
    def "create an empty StudentStats and get it from teacherdashboard with the course"() {
        when: "a studentStats is created"
        newStudentStats(teacherDashboard,externalCourseExecution)
        def newCE = newCourseExecution("123")

        then: "an empty studentStats is created and get from teacherdashboard with the course"
        studentStatsRepository.count() == 1L
        def result = studentStatsRepository.findAll().get(0)
        teacherDashboard.getCourseExecutionStudentStats(externalCourseExecution)==result
        teacherDashboard.getCourseExecutionStudentStats(newCE)==null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}

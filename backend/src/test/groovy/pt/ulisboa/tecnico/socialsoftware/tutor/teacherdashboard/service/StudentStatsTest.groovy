package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.StudentDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course

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

    @Unroll
    def "create an empty StudentStats"() {
        when: "a studentStats is created"
        def studentStats = new StudentStats(externalCourseExecution, teacherDashboard)
        studentStatsRepository.save(studentStats)
        teacherDashboard.addStudentStats(studentStats)
        then: "an empty dashboard is created"
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
        def studentStats = new StudentStats(externalCourseExecution, teacherDashboard)
        //studentStatsRepository.save(studentStats)
        teacherDashboard.addStudentStats(studentStats)
        then: "an empty studentStats is created and deleted"
        //studentStatsRepository.count() == 1L
        def result = studentStats//studentStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacherDashboard().getId() == teacherDashboard.getId()
        studentStats.remove()
        and: "the teacherDashboard has no reference for the studentStats"
        teacherDashboard.getStudentStats().size() == 0
    }

    @Unroll
    def "create an empty StudentStats and updated with empty course"() {
        when: "a studentStats is created"
        def studentStats = new StudentStats(externalCourseExecution, teacherDashboard)
        //studentStatsRepository.save(studentStats)
        teacherDashboard.addStudentStats(studentStats)
        then: "an empty studentStats is created and updated with empty course"
        //studentStatsRepository.count() == 1L
        def result = studentStats//studentStatsRepository.findAll().get(0)
        studentStats.update()
        result.getNumStudent() == 0
        result.getNumMore75CorrectQuestions() == 0
        result.getNumAtLeast3Quizzes() == 0
    }


    @Unroll
    def "create an empty StudentStats and updated with course that as 1 empty student"() {
        when: "a studentStats is created"
        def studentStats = new StudentStats(externalCourseExecution, teacherDashboard)
        //studentStatsRepository.save(studentStats)
        teacherDashboard.addStudentStats(studentStats)
        def student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student.addCourse(externalCourseExecution)
        def studentDashboard = new StudentDashboard(externalCourseExecution, student)
        studentDashboardRepository.save(studentDashboard)
        then: "an empty studentStats is created and updated course that as 1 empty student"
        //studentStatsRepository.count() == 1L
        def result = studentStats//studentStatsRepository.findAll().get(0)

        studentStats.update()
        result.getNumStudent() == 1
        result.getNumMore75CorrectQuestions() == 0
        result.getNumAtLeast3Quizzes() == 0
    }

    @Unroll
    def "create an empty StudentStats and updated with course that as 1 student but student does not have it"() {
        when: "a studentStats is created and a studentDashboard"
        def studentStats = new StudentStats(externalCourseExecution, teacherDashboard)
        //studentStatsRepository.save(studentStats)
        teacherDashboard.addStudentStats(studentStats)
        def student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student.addCourse(externalCourseExecution)
        def newCourse = new Course(COURSE_1_NAME, Course.Type.TECNICO)
        def newCourseExecution = new CourseExecution(newCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)

        def studentdashboard = new StudentDashboard(newCourseExecution, student)
        studentDashboardRepository.save(studentdashboard)
        then: "an empty studentStats is created and updated course that as 1 student but student does not have it"
        //studentStatsRepository.count() == 1L
        def result = studentStats//studentStatsRepository.findAll().get(0)

        studentStats.update()
        result.getNumStudent() == 0
        result.getNumMore75CorrectQuestions() == 0
        result.getNumAtLeast3Quizzes() == 0
    }



    @Unroll
    def "create an empty StudentStats and get from teacherdashboard with the course"() {
        when: "a studentStats is created"
        def studentStats = new StudentStats(externalCourseExecution, teacherDashboard)
        studentStatsRepository.save(studentStats)
        teacherDashboard.addStudentStats(studentStats)
        def newCourse = new Course("123", Course.Type.TECNICO)
        courseRepository.save(newCourse)
        def newCourseExecution  = new CourseExecution(newCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(newCourseExecution)
        then: "an empty studentStats is created and get from teacherdashboard with the course"
        studentStatsRepository.count() == 1L
        def result = studentStatsRepository.findAll().get(0)
        teacherDashboard.getCourseExecutionStudentStats(externalCourseExecution)==result
        teacherDashboard.getCourseExecutionStudentStats(newCourseExecution)==null
    }

    @Unroll
    def "create an empty StudentStats and add it 2 times to teacherDashboard"() {
        when: "a studentStats is created"
        def studentStats = new StudentStats(externalCourseExecution, teacherDashboard)
        studentStatsRepository.save(studentStats)
        teacherDashboard.addStudentStats(studentStats)
        then: "an empty dashboard is created"
        studentStatsRepository.count() == 1L
        def result = studentStatsRepository.findAll().get(0)
        result ==studentStats
        def c=0;
        try {
            teacherDashboard.addStudentStats(result)
        }
        catch(Exception STUDENT_STATS_ALREADY_CREATED) {
            c=1
        }
        c==1

    }
    @Unroll
    def "create 2 empty StudentStats and add them to teacherDashboard"() {
        when: "a studentStats is created"
        def studentStats1 = new StudentStats(externalCourseExecution, teacherDashboard)
        studentStatsRepository.save(studentStats1)
        def newCourse = new Course("123", Course.Type.TECNICO)
        courseRepository.save(newCourse)
        def newCourseExecution  = new CourseExecution(newCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(newCourseExecution)
        def studentStats2 = new StudentStats(newCourseExecution, teacherDashboard)
        studentStatsRepository.save(studentStats2)

        then: "add to teacherDashboard"
        teacherDashboard.addStudentStats(studentStats1)
        teacherDashboard.addStudentStats(studentStats2)
        teacherDashboard.getCourseExecutionStudentStats(externalCourseExecution)==studentStats1
        teacherDashboard.getCourseExecutionStudentStats(newCourseExecution)==studentStats2
    }
    /* @Unroll
     def "create an empty StudentStats and updated with course that has stats "() {
         when: "a studentStats is created"
         def studentStats = new StudentStats(externalCourseExecution, teacherDashboard)
         //studentStatsRepository.save(studentStats) perguntar
         teacherDashboard.addStudentStats(studentStats)
         def student1 = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
         def student2 = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
         student1.addCourse(externalCourseExecution)
         student2.addCourse(externalCourseExecution)
         def studentdashboard1 = new StudentDashboard(externalCourseExecution, student1)
         def studentdashboard2 = new StudentDashboard(externalCourseExecution, student2)
         studentDashboardRepository.save(studentdashboard1)
         studentDashboardRepository.save(studentdashboard2)
         def quiz = createQuiz()
         def quizQuestion = createQuizQuestion(quiz, question)
         def questionAnswer = answerQuiz(true, false, true, quizQuestion, quiz)
         studentdashboard1.statistics(questionAnswer)
         then: "an empty studentStats is created and updated course that as 1 empty student"
         //studentStatsRepository.count() == 1L

         def result = studentStats//studentStatsRepository.findAll().get(0)

         studentStats.update()
         result.getNumStudent() == 2
         result.getNumMore75CorrectQuestions() == 1
         result.getNumAtLeast3Quizzes() == 0
     }*/

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}

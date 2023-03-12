package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course

import java.time.LocalDateTime;

import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.StudentDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
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
class StudentStatsServiceTest extends SpockTest {
    def teacher
    def course

    def setup() {
        course = new Course("name", Course.Type.TECNICO)
        courseRepository.save(course)
        teacher = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher)
        courseRepository.save(course)
    }

    def localDateTime(year) {
        return LocalDateTime.of(year, 3, 29, 19, 30, 40);
    }

    def newCourseExecution(year) {
        def newCE  = new CourseExecution(course, COURSE_1_ACRONYM, "1 Semestre "+year-1+"/"+year, Course.Type.TECNICO, localDateTime(year))
        courseExecutionRepository.save(newCE)
        course.addCourseExecution(newCE)
        courseRepository.save(course)
        return newCE
    }

    def compareStudentStats(st,num,val1,val2)
    {
        return st.getNumStudent() == num &&
        st.getNumMore75CorrectQuestions() == val1 &&
        st.getNumAtLeast3Quizzes() == val2
    }
    @Unroll
    def "create an empty dashboard with 1 courseExecution"() {
        when: "a dashboard is created"
        CourseExecution newce = newCourseExecution(2023)
        teacher.addCourse(newce)
        teacherDashboardService.getTeacherDashboard(newce.getId(), teacher.getId())

        then: "an empty dashboard is created with 1 courseExecution"
        teacherDashboardRepository.count() == 1L
        def result = teacherDashboardRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == newce.getId()
        result.getTeacher().getId() == teacher.getId()

        result.getStudentStats().size()==1
        result.getStudentStats()[0].getCourseExecution().getEndDate().getYear()==2023
    }
    @Unroll
    def "create an empty dashboard with 3 courseExecution"() {
        when: "a dashboard is created"
        CourseExecution newce = newCourseExecution(2023)
        teacher.addCourse(newce)
        newCourseExecution(2022)
        newCourseExecution(2021)
        teacherDashboardService.getTeacherDashboard(newce.getId(), teacher.getId())

        then: "an empty dashboard is created with 1 courseExecution"
        teacherDashboardRepository.count() == 1L
        def result = teacherDashboardRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == newce.getId()
        result.getTeacher().getId() == teacher.getId()

        result.getStudentStats().size()==3
        result.getStudentStats().forEach(st ->
                        st.getCourseExecution().getEndDate().getYear()==2023 ||
                        st.getCourseExecution().getEndDate().getYear()==2022 ||
                        st.getCourseExecution().getEndDate().getYear()==2021
        )
    }

    @Unroll
    def "create an empty dashboard with 3 courseExecution 1 not in the last 3 years"() {
        when: "a dashboard is created"
        CourseExecution newce = newCourseExecution(2023)
        teacher.addCourse(newce)
        newCourseExecution(2021)
        newCourseExecution(2020)
        teacherDashboardService.getTeacherDashboard(newce.getId(), teacher.getId())

        then: "an empty dashboard is created with 2 courseExecution"
        teacherDashboardRepository.count() == 1L
        def result = teacherDashboardRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == newce.getId()
        result.getTeacher().getId() == teacher.getId()

        result.getStudentStats().size()==2
        result.getStudentStats().forEach(st ->
                st.getCourseExecution().getEndDate().getYear()==2023 ||
                        st.getCourseExecution().getEndDate().getYear()==2022 ||
                        st.getCourseExecution().getEndDate().getYear()==2021
        )
    }
    @Unroll
    def "create a Dashboard with 2 CourseExecutions and remove it"() {
        when: "a dashboard is created"
        CourseExecution newce = newCourseExecution(2023)
        teacher.addCourse(newce)
        newCourseExecution(2021)
        newCourseExecution(2020)
        teacherDashboardService.getTeacherDashboard(newce.getId(), teacher.getId())

        then: "a dashboard is created with 2 courseExecution and remove it"
        teacherDashboardRepository.count() == 1L
        studentStatsRepository.count() == 2L
        def td = teacherDashboardRepository.findAll().get(0)
        def studentS = studentStatsRepository.findAll().get(0)
        td.getId() != 0
        td.getCourseExecution().getId() == newce.getId()
        td.getTeacher().getId() == teacher.getId()
        td.getStudentStats().size()==2
        td.getStudentStats().forEach(st ->
                st.getCourseExecution().getEndDate().getYear()==2023 ||
                        st.getCourseExecution().getEndDate().getYear()==2022 ||
                        st.getCourseExecution().getEndDate().getYear()==2021
        )
        studentS.getTeacherDashboard().getId() == td.getId()
        studentS.getCourseExecution().getId() == td.getStudentStats()[0].getCourseExecution().getId() ||
        studentS.getCourseExecution().getId() == td.getStudentStats()[1].getCourseExecution().getId()

        teacherDashboardService.removeTeacherDashboard(td.getId())
        studentStatsRepository.count() == 0L
        teacherDashboardRepository.count() == 0L
    }



    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}


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

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto.QuestionStatsDto

import spock.lang.Unroll

@DataJpaTest
class QuestionStatsServiceTest extends SpockTest {
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

    def tt(st,a1,a2,a3){
        return st.getCourseExecution().getEndDate().getYear()==a1 ||st.getCourseExecution().getEndDate().getYear()==a2 || st.getCourseExecution().getEndDate().getYear()==a3
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
    def "create an empty dashboard with 1 courseExecution" () {

        when: "a dashboard is created"
        CourseExecution newce = newCourseExecution(2023)
        teacher.addCourse(newce)
        TeacherDashboardService.getTeacherDashboard(newce.getId(), teacher.getId())

        then: "an empty dashboard is created with 1 courseExecution"
        teacherDashboardRepository.count() == 1L
        def result = teacherDashboardRepository.findAll().get(0)

        result.getId() != 0
        result.getCourseExecution().getId() == newce.getId()
        result.getTeacher().getId() == teacher.getId()

        result.getQuestionStats().size()==1
        result.getQuestionStats()[0].getCourseExecution().getEndDate().getYear()==2023
    }

    @Unroll
    def "create an empty dashboard with 3 courseExecution and one that does not enter the list"() {
        when: "a dashboard is created"
        CourseExecution newce = newCourseExecution(2023)
        teacher.addCourse(newce)
        newCourseExecution(2022)
        newCourseExecution(2021)
        newCourseExecution(2020)
        teacherDashboardService.getTeacherDashboard(newce.getId(), teacher.getId())

        then: "an empty dashboard is created with 3 courseExecution"
        teacherDashboardRepository.count() == 1L
        def result = teacherDashboardRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == newce.getId()
        result.getTeacher().getId() == teacher.getId()

        result.getQuestionStats().size()==3
        result.getQuestionStats().stream().filter(st ->
                tt(st,2023,2022,2021)
        ).count() == 3
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

        result.getStudentStats().size()==3
        result.getStudentStats().stream().filter(st ->
                tt(st,2023,2022,2021)
        ).count() == 2
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
        questionStatsRepository.count() == 3L
        def td = teacherDashboardRepository.findAll().get(0)
        def questions = questionStatsRepository.findAll().get(0)
        td.getId() != 0
        td.getCourseExecution().getId() == newce.getId()
        td.getTeacher().getId() == teacher.getId()
        td.getQuestionStats().size()==3
        td.getQuestionStats().stream().filter(st ->
                tt(st,2023,2022,2021)
        ).count() == 2
        questions.getTeacherDashboard().getId() == td.getId()
        questions.getCourseExecution().getId() == td.getQuestionStats()[0].getCourseExecution().getId() ||
        questions.getCourseExecution().getId() == td.getQuestionStats()[1].getCourseExecution().getId() ||
        questions.getCourseExecution().getId() == td.getQuestionStats()[2].getCourseExecution().getId()

        teacherDashboardService.removeTeacherDashboard(td.getId())
        questionStatsRepository.count() == 0L
        teacherDashboardRepository.count() == 0L
    }


}

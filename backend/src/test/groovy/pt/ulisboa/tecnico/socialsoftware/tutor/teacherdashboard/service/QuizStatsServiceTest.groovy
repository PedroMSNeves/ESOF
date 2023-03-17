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

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto.QuizStatsDto;

@DataJpaTest
class QuizStatsServiceTest extends SpockTest {
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

    def compareQuizStats(qs,num,val1,val2)
    {
        return qs.getNumQuizzes() == num &&
                qs.getUniqueQuizzesSolved() == val1 &&
                qs.getAverageQuizzesSolved() == val2
    }

    def newQuiz(courseExecution) {
        def quiz = new Quiz()
        quiz.setCourseExecution(courseExecution)
        quizRepository.save(quiz)
        return quiz
    }

    def newStudent(courseExecution, username){
        def student = new Student(USER_1_NAME, username, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student.addCourse(courseExecution)
        userRepository.save(student)
        def studentDashboard = new StudentDashboard(courseExecution, student)
        studentDashboardRepository.save(studentDashboard)
        return student
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

    def compareQuizzesStatsandDto(qs,qsdto,num,val1,val2) {
        return qs.getNumQuizzes() == num && qsdto.getNumQuizzes() == num &&
                qs.getUniqueQuizzesSolved() == val1 && qsdto.getUniqueQuizzesSolved() == val1 &&
                qs.getAverageQuizzesSolved() == val2 && qsdto.getAverageQuizzesSolved() == val2
    }


    def testToString(qs,qsdto) {
        return qsdto.toString() == "QuizStatsDto{" + "id=" + qs.getId() + ", numQuizzes=" + qs.getNumQuizzes() +
                ", numUniqueQuizzesSolved=" + qs.getUniqueQuizzesSolved() +
                ", numAverageQuizzesSolved=" + qs.getAverageQuizzesSolved() + '}'
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

        result.getQuizStats().size()==1
        result.getQuizStats()[0].getCourseExecution().getEndDate().getYear()==2023
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

        result.getQuizStats().size()==3
        result.getQuizStats().stream().filter(st ->
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

        result.getQuizStats().size()==3
        result.getQuizStats().stream().filter(st ->
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
        quizStatsRepository.count() == 3L
        def td = teacherDashboardRepository.findAll().get(0)
        def quizS = quizStatsRepository.findAll().get(0)
        td.getId() != 0
        td.getCourseExecution().getId() == newce.getId()
        td.getTeacher().getId() == teacher.getId()
        td.getQuizStats().size()==3
        td.getQuizStats().stream().filter(st ->
                tt(st,2023,2022,2021)
        ).count() == 2
        quizS.getTeacherDashboard().getId() == td.getId()
        quizS.getCourseExecution().getId() == td.getQuizStats()[0].getCourseExecution().getId() ||
                quizS.getCourseExecution().getId() == td.getQuizStats()[1].getCourseExecution().getId() ||
                quizS.getCourseExecution().getId() == td.getQuizStats()[2].getCourseExecution().getId()

        teacherDashboardService.removeTeacherDashboard(td.getId())
        quizStatsRepository.count() == 0L
        teacherDashboardRepository.count() == 0L
    }

    @Unroll
    def "create an empty dashboard and update it"() {
        when: "a dashboard is created"
        CourseExecution newce = newCourseExecution(2023)
        teacher.addCourse(newce)
        CourseExecution newce2 = newCourseExecution(2021)
        newCourseExecution(2020)
        teacherDashboardService.getTeacherDashboard(newce.getId(), teacher.getId())
        newQuiz(newce);

        then: "an empty dashboard is created with 2 courseExecution"
        teacherDashboardRepository.count() == 1L
        quizStatsRepository.count() == 3L
        def td = teacherDashboardRepository.findAll().get(0)
        def quizS
        quizStatsRepository.findAll().forEach(qs -> {
            if (qs.getCourseExecution().getId() == newce.getId())
                quizS = qs
        })
        td.getId() != 0
        compareQuizStats(quizS, 0, 0, 0) //should all be 0 because we have not updated
        //se quiseres depois adiciona aqui mais alteracoes
        teacherDashboardService.updateTeacherDashboard(td.getId())
        def quizS1
        def quizS2
        def quizS3
        quizStatsRepository.findAll().forEach(qs -> {
            if (qs.getCourseExecution().getId() == newce.getId())
                quizS1 = qs
            else if (qs.getCourseExecution().getId() == newce2.getId())
                quizS2 = qs
            else
                quizS3 = qs
        })
        compareQuizStats(quizS1, 1, 0, 0) //now that we have updated it we should have some values that ar differend
        compareQuizStats(quizS2, 0, 0, 0)
        compareQuizStats(quizS3, 0, 0, 0)
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}

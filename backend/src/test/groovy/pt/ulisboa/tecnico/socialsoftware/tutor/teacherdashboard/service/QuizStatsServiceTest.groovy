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
                ", uniqueQuizzesSolved=" + qs.getUniqueQuizzesSolved() +
                ", averageQuizzesSolved=" + qs.getAverageQuizzesSolved() + '}'
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

    //CHANGE IT TO HAVE QUIZZES STATS
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

    @Unroll
    def "try to update a dashboard that does not exist"() {
        when: "flag created"
        def e=0

        then: "update a dashboard that does not exist"
        try {
            teacherDashboardService.updateTeacherDashboard(1)
        }
        catch(Exception ex) {
            e=1
        }
        e==1
    }

    //CHANGE IT TO HAVE QUIZ
    @Unroll
    def "create 2 dashboards and update them"() {
        when: "a dashboard is created"

        CourseExecution newce1 = newCourseExecution(2021)
        teacher.addCourse(newce1)
        teacherDashboardService.getTeacherDashboard(newce1.getId(), teacher.getId())
        CourseExecution newce2 = newCourseExecution(2023)
        teacher.addCourse(newce2)
        teacherDashboardService.getTeacherDashboard(newce2.getId(), teacher.getId())

        def quiz = newQuiz(newce1)
        def student = newStudent(newce1,"toni")
        createQuizAnswer(student, quiz)

        def student2 = newStudent(newce2,"inot")
        createQuizAnswer(student2, newQuiz(newce2))
        createQuizAnswer(student2, newQuiz(newce2))

        then: "update 2 dashboards"
        teacherDashboardRepository.count() == 2L
        quizStatsRepository.count() == 3L
        def td1
        def td2
        teacherDashboardRepository.findAll().forEach(td ->{
            if(td.getCourseExecution().getId() == newce1.getId())
                td1=td
            if(td.getCourseExecution().getId() == newce2.getId())
                td2=td
        })
        td1.getId() != 0
        td2.getId() != 0
        def quizS1
        def quizS2
        def quizS3

        teacherDashboardService.updateAllTeacherDashboards()
        quizStatsRepository.findAll().forEach(qs -> {
            if (qs.getTeacherDashboard().getId() == td1.getId())
                quizS1=qs
            if (qs.getTeacherDashboard().getId() == td2.getId() && qs.getCourseExecution().getId() == newce1.getId())
                quizS2=qs
            if (qs.getTeacherDashboard().getId() == td2.getId() && qs.getCourseExecution().getId() == newce2.getId())
                quizS3=qs
        })

        compareQuizStats(quizS1,1,1,1)
        compareQuizStats(quizS2,1,1,1)
        compareQuizStats(quizS3,2,2,2)
    }

    //CHANGE IT TO HAVE QUIZ
    @Unroll
    def "create a 2 empty dashboard and update them with 2 courses execution each"() {
        when: "a dashboard is created"
        CourseExecution newce0 = newCourseExecution(2020)
        CourseExecution newce1 = newCourseExecution(2021)
        teacher.addCourse(newce1)
        teacherDashboardService.getTeacherDashboard(newce1.getId(), teacher.getId())
        CourseExecution newce2 = newCourseExecution(2023)
        teacher.addCourse(newce2)
        teacherDashboardService.getTeacherDashboard(newce2.getId(), teacher.getId())

        //one student in a course execution that solves 2 unique quizzes
        def quiz00 = newQuiz(newce0)
        def quiz01 = newQuiz(newce0)
        def student0 = newStudent(newce0, "toni")
        createQuizAnswer(student0, quiz00)
        createQuizAnswer(student0, quiz01)

        //two students in a course execution with 2 unique tests each
        def student11 = newStudent(newce1, "ze");
        def student12 = newStudent(newce1, "ez");
        for(int i = 0; i < 2; i++) {
            createQuizAnswer(student11, newQuiz(newce1))
        }
        for(int i = 0; i < 2; i++) {
            createQuizAnswer(student12, newQuiz(newce1))
        }

        //def rp2 =newStudent(newce2,"rasputin2")
        //for(int i=3;i<7;i++){ newStudent(newce2,"bot"+i)}

        then: "an empty dashboard is created with 1 courseExecution"
        teacherDashboardRepository.count() == 2L
        quizStatsRepository.count() == 5L
        def td1
        def td2
        teacherDashboardRepository.findAll().forEach(td ->{
            if(td.getCourseExecution().getId() == newce1.getId())
                td1=td
            if(td.getCourseExecution().getId() == newce2.getId())
                td2=td
        })
        td1.getId() != 0
        td2.getId() != 0
        def quizS0
        def quizS1
        def quizS2
        def quizS3
        def quizS4

        teacherDashboardService.updateAllTeacherDashboards()
        quizStatsRepository.findAll().forEach(st -> {
            if (st.getTeacherDashboard().getId() == td1.getId() && st.getCourseExecution().getId() == newce0.getId())
                quizS0=st
            if (st.getTeacherDashboard().getId() == td1.getId() && st.getCourseExecution().getId() == newce1.getId())
                quizS1=st
            if (st.getTeacherDashboard().getId() == td2.getId() && st.getCourseExecution().getId() == newce1.getId())
                quizS2=st
            if (st.getTeacherDashboard().getId() == td2.getId() && st.getCourseExecution().getId() == newce2.getId())
                quizS3=st
            if (st.getTeacherDashboard().getId() == td2.getId() && st.getCourseExecution().getId() == newce0.getId())
                quizS4=st
        })

        //depois coloca aqui os dados como deve ser
        compareQuizStats(quizS0,2,2,2)
        compareQuizStats(quizS1,4,4,2)
        compareQuizStats(quizS2,4,4,2)
        compareQuizStats(quizS3,0,0,0)
        compareQuizStats(quizS4,2,2,2)
    }

    @Unroll
    def "create an empty dashboard and update them with 3 courses execution each to see Dto"() {
        when: "a dashboard is created"
        CourseExecution newce0 = newCourseExecution(2020)
        CourseExecution newce1 = newCourseExecution(2021)
        CourseExecution newce2 = newCourseExecution(2023)
        teacher.addCourse(newce2)
        teacherDashboardService.getTeacherDashboard(newce2.getId(), teacher.getId())

        def quiz00 = newQuiz(newce0)
        def quiz01 = newQuiz(newce0)
        def student0 = newStudent(newce0, "toni")
        createQuizAnswer(student0, quiz00)
        createQuizAnswer(student0, quiz01)

        //two students in a course execution with 2 unique tests each
        def student11 = newStudent(newce1, "ze");
        def student12 = newStudent(newce1, "ez");
        for(int i = 0; i < 2; i++) {
            createQuizAnswer(student11, newQuiz(newce1))
        }
        for(int i = 0; i < 2; i++) {
            createQuizAnswer(student12, newQuiz(newce1))
        }

        then: "update a dashboard and compare the Dto"
        teacherDashboardRepository.count() == 1L
        quizStatsRepository.count() == 3L
        def td2 = teacherDashboardRepository.findAll().get(0)
        td2.getId() != 0
        def quizS2
        def quizS3
        def quizS4

        teacherDashboardService.updateAllTeacherDashboards()
        quizStatsRepository.findAll().forEach(qs -> {
            if (qs.getTeacherDashboard().getId() == td2.getId() && qs.getCourseExecution().getId() == newce1.getId()) {
                quizS2=qs
            }
            if (qs.getTeacherDashboard().getId() == td2.getId() && qs.getCourseExecution().getId() == newce2.getId()) {
                quizS3=qs
            }
            if (qs.getTeacherDashboard().getId() == td2.getId() && qs.getCourseExecution().getId() == newce0.getId()) {
                quizS4=qs
            }
        })
        quizS4.getNumQuizzes() == 2
        def teacherDashboardDto = teacherDashboardService.getTeacherDashboard(newce2.getId(), teacher.getId())
        def quizStatsDto1
        def quizStatsDto2
        def quizStatsDto3
        teacherDashboardDto.getQuizStatsDtos().stream().forEach(qsDto ->{
            if(qsDto.getId()== quizS2.getId())
                quizStatsDto1=qsDto
            else if (qsDto.getId()== quizS3.getId())
                quizStatsDto2=qsDto
            else if (qsDto.getId()== quizS4.getId())
                quizStatsDto3=qsDto
        })

        compareQuizzesStatsandDto(quizS2,quizStatsDto1,4,4,2.0)
        testToString(quizS2,quizStatsDto1)
        compareQuizzesStatsandDto(quizS3,quizStatsDto2,0,0,0.0)
        compareQuizzesStatsandDto(quizS4,quizStatsDto3,2,2,2.0)

        and: "testToString"
        testToString(quizS3,quizStatsDto2)
        testToString(quizS4,quizStatsDto3)
        and: "testCreateByHand a QuizzesStatsDto"
        def manual = new QuizStatsDto()
        manual.setId(quizS2.getId())
        manual.setNumQuizzes(quizS2.getNumQuizzes());
        manual.setUniqueQuizzesSolved(quizS2.getUniqueQuizzesSolved());
        manual.setAverageQuizzesSolved(quizS2.getAverageQuizzesSolved());
        manual.toString()== quizStatsDto1.toString()
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
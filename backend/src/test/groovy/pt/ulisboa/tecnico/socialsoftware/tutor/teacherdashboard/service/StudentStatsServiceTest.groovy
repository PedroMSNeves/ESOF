package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
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

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto.StudentStatsDto

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

        result.getStudentStats().size()==3
        result.getStudentStats().stream().filter(st ->
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

        then: "an empty dashboard is created with 3 courseExecution"
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
    def "create a Dashboard with 3 CourseExecutions and remove it"() {
        when: "a dashboard is created"
        CourseExecution newce = newCourseExecution(2023)
        teacher.addCourse(newce)
        newCourseExecution(2021)
        newCourseExecution(2020)
        teacherDashboardService.createTeacherDashboard(newce.getId(), teacher.getId())

        then: "a dashboard is created with 3 courseExecution and remove it"
        teacherDashboardRepository.count() == 1L
        studentStatsRepository.count() == 3L
        def td = teacherDashboardRepository.findAll().get(0)
        def studentS = studentStatsRepository.findAll().get(0)
        td.getId() != 0
        td.getCourseExecution().getId() == newce.getId()
        td.getTeacher().getId() == teacher.getId()
        td.getStudentStats().size()==3
        td.getStudentStats().stream().filter(st ->
                tt(st,2023,2022,2021)
        ).count() == 2
        studentS.getTeacherDashboard().getId() == td.getId()
        studentS.getCourseExecution().getId() == td.getStudentStats()[0].getCourseExecution().getId() ||
                studentS.getCourseExecution().getId() == td.getStudentStats()[1].getCourseExecution().getId() ||
                studentS.getCourseExecution().getId() == td.getStudentStats()[2].getCourseExecution().getId()

        teacherDashboardService.removeTeacherDashboard(td.getId())
        studentStatsRepository.count() == 0L
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
        quizz(newStudent(newce,"rasputin"),true,true,newce)

        then: "an empty dashboard is created with 3 courseExecution"
        teacherDashboardRepository.count() == 1L
        studentStatsRepository.count() == 3L
        def td = teacherDashboardRepository.findAll().get(0)
        def studentS
        studentStatsRepository.findAll().forEach(st -> {
            if (st.getCourseExecution().getId() == newce.getId())
                studentS = st
        })
        td.getId() != 0
        compareStudentStats(studentS,0,0,0)
        teacherDashboardService.updateTeacherDashboard(td.getId())
        def studentS1
        def studentS2
        def studentS3
        studentStatsRepository.findAll().forEach(st -> {
            if (st.getCourseExecution().getId() == newce.getId())
                studentS1 = st
            else if (st.getCourseExecution().getId() == newce2.getId())
                studentS2 = st
            else
                studentS3 = st
        })
        compareStudentStats(studentS1,1,1,0)
        compareStudentStats(studentS2,0,0,0)
        compareStudentStats(studentS3,0,0,0)
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

    @Unroll
    def "create 2 dashboards and update them"() {
        when: "a dashboard is created"

        CourseExecution newce1 = newCourseExecution(2021)
        teacher.addCourse(newce1)
        teacherDashboardService.getTeacherDashboard(newce1.getId(), teacher.getId())
        CourseExecution newce2 = newCourseExecution(2023)
        teacher.addCourse(newce2)
        teacherDashboardService.getTeacherDashboard(newce2.getId(), teacher.getId())

        quizz(newStudent(newce1,"rasputin1"),true,true,newce1)
        quizz(newStudent(newce2,"rasputin2"),true,false,newce2)
        then: "update 2 dashboards"
        teacherDashboardRepository.count() == 2L
        studentStatsRepository.count() == 3L
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
        def studentS1
        def studentS2
        def studentS3

        teacherDashboardService.updateAllTeacherDashboards()
        studentStatsRepository.findAll().forEach(st -> {
            if (st.getTeacherDashboard().getId() == td1.getId())
                studentS1=st
            if (st.getTeacherDashboard().getId() == td2.getId() && st.getCourseExecution().getId() == newce1.getId())
                studentS2=st
            if (st.getTeacherDashboard().getId() == td2.getId() && st.getCourseExecution().getId() == newce2.getId())
                studentS3=st
        })

        compareStudentStats(studentS1,1,1,0)
        compareStudentStats(studentS2,1,1,0)
        compareStudentStats(studentS3,1,0,0)
    }


    @Unroll
    def "create a 2 empty dashboard and update them: complex edition"() {
        when: "a dashboard is created"
        CourseExecution newce0 = newCourseExecution(2020)
        CourseExecution newce1 = newCourseExecution(2021)
        teacher.addCourse(newce1)
        teacherDashboardService.getTeacherDashboard(newce1.getId(), teacher.getId())
        CourseExecution newce2 = newCourseExecution(2023)
        teacher.addCourse(newce2)
        teacherDashboardService.getTeacherDashboard(newce2.getId(), teacher.getId())
        def rp0 =newStudent(newce0,"rasputin0")
        newStudent(newce0,"bot0")
        def rp1 =newStudent(newce1,"rasputin1")
        for(int i=1;i<3;i++){ newStudent(newce1,"bot"+i)}
        def rp2 =newStudent(newce2,"rasputin2")
        for(int i=3;i<7;i++){ newStudent(newce2,"bot"+i)}

        for(int i =0;i<3;i++) {
            for(int j =0;j<4;j++) {quizz(rp0,true,true,newce0)}
            quizz(rp0,true,false,newce0)
            for(int j =0;j<4;j++) {quizz(rp1,true,false,newce1)}
            quizz(rp1,true,true,newce1)
        }
        quizz(rp2,true,true,newce2)
        then: "update 2 dashboards"
        teacherDashboardRepository.count() == 2L
        studentStatsRepository.count() == 5L
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
        def studentS0
        def studentS1
        def studentS2
        def studentS3
        def studentS4

        teacherDashboardService.updateAllTeacherDashboards()
        studentStatsRepository.findAll().forEach(st -> {
            if (st.getTeacherDashboard().getId() == td1.getId() && st.getCourseExecution().getId() == newce0.getId())
                studentS0=st
            if (st.getTeacherDashboard().getId() == td1.getId() && st.getCourseExecution().getId() == newce1.getId())
                studentS1=st
            if (st.getTeacherDashboard().getId() == td2.getId() && st.getCourseExecution().getId() == newce1.getId())
                studentS2=st
            if (st.getTeacherDashboard().getId() == td2.getId() && st.getCourseExecution().getId() == newce2.getId())
                studentS3=st
            if (st.getTeacherDashboard().getId() == td2.getId() && st.getCourseExecution().getId() == newce0.getId())
                studentS4=st
        })
        compareStudentStats(studentS0,2,1,1)
        compareStudentStats(studentS1,3,0,1)
        compareStudentStats(studentS2,3,0,1)
        compareStudentStats(studentS3,5,1,0)
        compareStudentStats(studentS4,2,1,1)
    }




    @Unroll
    def "create an empty dashboard and update them with 3 courses execution each to see Dto"() {
        when: "a dashboard is created"
        CourseExecution newce0 = newCourseExecution(2020)
        CourseExecution newce1 = newCourseExecution(2021)
        CourseExecution newce2 = newCourseExecution(2023)
        teacher.addCourse(newce2)
        teacherDashboardService.getTeacherDashboard(newce2.getId(), teacher.getId())
        def rp0 =newStudent(newce0,"rasputin0")
        newStudent(newce0,"bot0")
        def rp1 =newStudent(newce1,"rasputin1")
        for(int i=1;i<3;i++){ newStudent(newce1,"bot"+i)}
        def rp2 =newStudent(newce2,"rasputin2")
        for(int i=3;i<7;i++){ newStudent(newce2,"bot"+i)}

        for(int i =0;i<3;i++) {
            for(int j =0;j<4;j++) {quizz(rp0,true,true,newce0)}
            quizz(rp0,true,false,newce0)
            for(int j =0;j<4;j++) {quizz(rp1,true,false,newce1)}
            quizz(rp1,true,true,newce1)
        }
        quizz(rp2,true,true,newce2)
        then: "update a dashboard and compare the Dto"
        teacherDashboardRepository.count() == 1L
        studentStatsRepository.count() == 3L
        def td2 = teacherDashboardRepository.findAll().get(0)
        td2.getId() != 0
        def studentS2
        def studentS3
        def studentS4

        teacherDashboardService.updateAllTeacherDashboards()
        studentStatsRepository.findAll().forEach(st -> {
            if (st.getTeacherDashboard().getId() == td2.getId() && st.getCourseExecution().getId() == newce1.getId())
                studentS2=st
            if (st.getTeacherDashboard().getId() == td2.getId() && st.getCourseExecution().getId() == newce2.getId())
                studentS3=st
            if (st.getTeacherDashboard().getId() == td2.getId() && st.getCourseExecution().getId() == newce0.getId())
                studentS4=st
        })
        def teacherDashboardDto = teacherDashboardService.getTeacherDashboard(newce2.getId(), teacher.getId())
        def studentStatsDto1
        def studentStatsDto2
        def studentStatsDto3
        teacherDashboardDto.getStudentStatsDtos().stream().forEach(stDto ->{
            if(stDto.getId()== studentS2.getId())
                studentStatsDto1=stDto
            else if (stDto.getId()== studentS3.getId())
                studentStatsDto2=stDto
            else if (stDto.getId()== studentS4.getId())
                studentStatsDto3=stDto
        })

        compareStudentStatsandDto(studentS2,studentStatsDto1,3,0,1)
        compareStudentStatsandDto(studentS3,studentStatsDto2,5,1,0)
        compareStudentStatsandDto(studentS4,studentStatsDto3,2,1,1)
        and: "testToString"
        testToString(studentS2,studentStatsDto1)
        testToString(studentS3,studentStatsDto2)
        testToString(studentS4,studentStatsDto3)
        and: "testCreateByHand a StudentStatsDto"
        def manual = new StudentStatsDto()
        manual.setId(studentS2.getId())
        manual.setNumStudent(studentS2.getNumStudent());
        manual.setNumMore75CorrectQuestions(studentS2.getNumMore75CorrectQuestions());
        manual.setNumAtLeast3Quizzes(studentS2.getNumAtLeast3Quizzes());
        manual.toString()== studentStatsDto1.toString()
    }


    @Unroll
    def "create a dashboard with a course that has a courseExecution with null date"() {
        when: "a dashboard is created"
        CourseExecution newCE = newCourseExecution(2020)
        newCE.setEndDate(null)
        courseExecutionRepository.save(newCE)

        CourseExecution newce1 = newCourseExecution(2021)
        CourseExecution newce2 = newCourseExecution(2023)
        teacher.addCourse(newce2)
        def rp1 =newStudent(newce1,"rasputin1")
        for(int i=1;i<3;i++){ newStudent(newce1,"bot"+i)}
        def rp2 =newStudent(newce2,"rasputin2")
        for(int i=3;i<7;i++){ newStudent(newce2,"bot"+i)}

        for(int i =0;i<3;i++) {
            for(int j =0;j<4;j++) {quizz(rp1,true,false,newce1)}
            quizz(rp1,true,true,newce1)
        }
        quizz(rp2,true,true,newce2)
        then: "an dashboard is created with 2 courseExecution"
        teacherDashboardRepository.count() == 0L
        studentStatsRepository.count() == 0L


        teacherDashboardService.updateAllTeacherDashboards()
        teacherDashboardRepository.count() == 1L
        studentStatsRepository.count() == 2L

        def td2 = teacherDashboardRepository.findAll().get(0)
        td2.getId() != 0
        def studentS2
        def studentS3

        studentStatsRepository.findAll().forEach(st -> {
            if (st.getTeacherDashboard().getId() == td2.getId() && st.getCourseExecution().getId() == newce1.getId())
                studentS2=st
            if (st.getTeacherDashboard().getId() == td2.getId() && st.getCourseExecution().getId() == newce2.getId())
                studentS3=st
        })
        def teacherDashboardDto = teacherDashboardService.getTeacherDashboard(newce2.getId(), teacher.getId())
        def studentStatsDto1
        def studentStatsDto2
        teacherDashboardDto.getStudentStatsDtos().stream().forEach(stDto ->{
            if(stDto.getId()== studentS2.getId())
                studentStatsDto1=stDto
            else if (stDto.getId()== studentS3.getId())
                studentStatsDto2=stDto
        })

        compareStudentStatsandDto(studentS2,studentStatsDto1,3,0,1)
        compareStudentStatsandDto(studentS3,studentStatsDto2,5,1,0)
        System.out.println(studentS2.getCourseExecution().getEndDate().getYear()+"  "+studentS3.getCourseExecution().getEndDate().getYear())
    }




    def testToString(st,stdto)
    {
        return stdto.toString() == "StudentStatsDto{" + "id=" + st.getId() + ", numStudent=" + st.getNumStudent() +
                ", numMore75CorrectQuestions=" + st.getNumMore75CorrectQuestions() +
                ", numAtLeast3Quizzes=" + st.getNumAtLeast3Quizzes() + '}';
    }
    def compareStudentStatsandDto(st,stdto,num,val1,val2)
    {
        return st.getNumStudent() == num && stdto.getNumStudent() == num &&
                st.getNumMore75CorrectQuestions() == val1 && stdto.getNumMore75CorrectQuestions() == val1 &&
                st.getNumAtLeast3Quizzes() == val2 && stdto.getNumAtLeast3Quizzes() == val2
    }

    // Method for creating a newStudent with a courseExecution
    def newStudent(courseExecution,username){
        def student = new Student(username, username, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student.addCourse(courseExecution)
        userRepository.save(student)
        def studentDashboard = new StudentDashboard(courseExecution, student)
        studentDashboardRepository.save(studentDashboard)
        return student
    }
    // Methods for creating a quiz
    def createQuestion() {
        def newQuestion = new Question()
        newQuestion.setTitle("Question Title")
        newQuestion.setCourse(course)
        def questionDetails = new MultipleChoiceQuestion()
        newQuestion.setQuestionDetails(questionDetails)
        questionRepository.save(newQuestion)

        def option = new Option()
        option.setContent("Option Content")
        option.setCorrect(true)
        option.setSequence(0)
        option.setQuestionDetails(questionDetails)
        optionRepository.save(option)
        def optionKO = new Option()
        optionKO.setContent("Option Content")
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)

        return newQuestion;
    }

    def createQuiz(ce,type = Quiz.QuizType.PROPOSED.toString()) {
        def quiz = new Quiz()
        quiz.setTitle("Quiz Title")
        quiz.setType(type)
        quiz.setCourseExecution(ce)
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

    def answerQuiz(answered, correct, completed, quizQuestion, quiz,student ,date = DateHandler.now()) {
        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(completed)
        quizAnswer.setCreationDate(date)
        quizAnswer.setAnswerDate(date)
        quizAnswer.setStudent(student)
        quizAnswer.setQuiz(quiz)
        quizAnswerRepository.save(quizAnswer)

        def questionAnswer = new QuestionAnswer()
        questionAnswer.setTimeTaken(1)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)

        def answerDetails
        def correctOption = quizQuestion.getQuestion().getQuestionDetails().getCorrectOption()
        def incorrectOption = quizQuestion.getQuestion().getQuestionDetails().getOptions().stream().filter(option -> option != correctOption).findAny().orElse(null)
        if (answered && correct) answerDetails = new MultipleChoiceAnswer(questionAnswer, correctOption)
        else if (answered && !correct) answerDetails = new MultipleChoiceAnswer(questionAnswer, incorrectOption)
        else {
            questionAnswerRepository.save(questionAnswer)
            return quizAnswer
        }
        questionAnswer.setAnswerDetails(answerDetails)
        answerDetailsRepository.save(answerDetails)
        return quizAnswer
    }
    def quizz(student,answered,correct, ce)
    {
        def question = createQuestion()
        def quiz = createQuiz(ce)
        def quizQuestion = createQuizQuestion(quiz, question)
        def quizzAnswer = answerQuiz(answered, correct, true, quizQuestion, quiz,student)
        student.getCourseExecutionDashboard(ce).statistics(quizzAnswer)
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}

package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
//import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
//import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.StudentDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
//import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
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
    def newCourseExecution()
    {
        def newCourse = new Course("123", Course.Type.TECNICO)
        courseRepository.save(newCourse)
        def newCE  = new CourseExecution(newCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(newCE)
        return newCE
    }

    @Unroll
    def "create an empty StudentStats"() {
        when: "a studentStats is created"
        newStudentStats(teacherDashboard,externalCourseExecution)

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
        when: "a studentStats is created"
        newstudent(externalCourseExecution,"dadsad")
        newStudentStats(teacherDashboard,externalCourseExecution)


        then: "an empty studentStats is created and updated course that as 1 empty student"
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
        def newCE = newCourseExecution()
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
    def "create an empty StudentStats and get from teacherdashboard with the course"() {
        when: "a studentStats is created"
        newStudentStats(teacherDashboard,externalCourseExecution)
        def newCE = newCourseExecution()

        then: "an empty studentStats is created and get from teacherdashboard with the course"
        studentStatsRepository.count() == 1L
        def result = studentStatsRepository.findAll().get(0)
        teacherDashboard.getCourseExecutionStudentStats(externalCourseExecution)==result
        teacherDashboard.getCourseExecutionStudentStats(newCE)==null
    }

    @Unroll
    def "create an empty StudentStats and add it 2 times to teacherDashboard"() {
        when: "a studentStats is created"
        newStudentStats(teacherDashboard,externalCourseExecution)

        then: "an empty dashboard is created"
        studentStatsRepository.count() == 1L
        def result = studentStatsRepository.findAll().get(0)
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
        when: "a studentStats is created and add to teacherDashboard"
        def studentStats1=newStudentStats(teacherDashboard,externalCourseExecution)
        def newCE = newCourseExecution()
        def studentStats2=newStudentStats(teacherDashboard,newCE)

        then: "see if added"
        teacherDashboard.getCourseExecutionStudentStats(externalCourseExecution)==studentStats1
        teacherDashboard.getCourseExecutionStudentStats(newCE)==studentStats2
    }

    @Unroll
    def "create empty studentStats and use toString"() {
        when: "a studentStats is created"
        def newTeacher = new Teacher(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        userRepository.save(newTeacher)
        def td = new TeacherDashboard(externalCourseExecution, newTeacher)
        teacherDashboardRepository.save(td)
        newStudentStats(td,externalCourseExecution)

        then: "compare toString"
        studentStatsRepository.count() == 1L
        def result = studentStatsRepository.findAll().get(0)
        result.toString()==td.getCourseExecutionStudentStats(externalCourseExecution).toString()
    }

    def createQuestion() {
        def newQuestion = new Question()
        newQuestion.setTitle("Question Title")
        newQuestion.setCourse(externalCourse)
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
    def quizz(student,answered,correct)
    {
        def question = createQuestion()
        def quiz = createQuiz()
        def quizQuestion = createQuizQuestion(quiz, question)
        def quizzAnswer = answerQuiz(answered, correct, true, quizQuestion, quiz,student)
        student.getCourseExecutionDashboard(externalCourseExecution).statistics(quizzAnswer)
    }
     @Unroll
     def "create an empty StudentStats populate it with less than 75 acc within 3 quizzes"() {
         when: "a studentStats is created and the quizzes"
         newStudentStats(teacherDashboard,externalCourseExecution)
         def student1 = newstudent(externalCourseExecution,"1")
         def student2 = newstudent(externalCourseExecution,"2")
         for(int i=0;i<3;i++) {
             quizz(student1,true,false)
         }

         then: "an empty studentStats is updated"
         studentStatsRepository.count() == 1L
         def result = studentStatsRepository.findAll().get(0)

         result.update()
         result.getNumStudent() == 2
         result.getNumMore75CorrectQuestions() == 0
         result.getNumAtLeast3Quizzes() == 1
     }

    @Unroll
    def "create an empty StudentStats populate it with less than 75 acc within 3 quizzes in 2 quizzes"() {
        when: "a studentStats is created and the quizzes"
        newStudentStats(teacherDashboard,externalCourseExecution)
        def student1 = newstudent(externalCourseExecution,"1")
        def student2 = newstudent(externalCourseExecution,"2")
        newstudent(externalCourseExecution,"3")
        for(int i=0;i<3;i++) {
            quizz(student1,true,false)
            quizz(student2,true,true)
        }

        then: "an empty studentStats is updated"
        studentStatsRepository.count() == 1L
        def result = studentStatsRepository.findAll().get(0)

        result.update()
        result.getNumStudent() == 3
        result.getNumMore75CorrectQuestions() == 1
        result.getNumAtLeast3Quizzes() == 2
    }

    @Unroll
    def "create an empty StudentStats populate it with more than 75 acc within 3 quizzes"() {
        when: "a studentStats is created and the quizzes"
        newStudentStats(teacherDashboard,externalCourseExecution)
        def student1 = newstudent(externalCourseExecution,"1")
        def student2 = newstudent(externalCourseExecution,"2")
        newstudent(externalCourseExecution,"3")
        for(int i=0;i<3;i++) {
            quizz(student1,true,true)
            quizz(student1,true,true)
            quizz(student1,true,true)
            quizz(student1,true,true)
            quizz(student1,true,false)
            quizz(student2,true,false)
        }

        then: "an empty studentStats is updated"
        studentStatsRepository.count() == 1L
        def result = studentStatsRepository.findAll().get(0)

        result.update()
        result.getNumStudent() == 3
        result.getNumMore75CorrectQuestions() == 1
        result.getNumAtLeast3Quizzes() == 2
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}

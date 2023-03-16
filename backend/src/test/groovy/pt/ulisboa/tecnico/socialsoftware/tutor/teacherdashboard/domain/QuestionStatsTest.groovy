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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.StudentDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course

import spock.lang.Unroll

@DataJpaTest
class QuestionStatsTest extends SpockTest {
    def teacherDashboard
    def teacher

    def setup() {
        createExternalCourseAndExecution()
        teacher = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher)
        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)
    }

    def newCourseExecution(name) {
        def newCourse = new Course(name, Course.Type.TECNICO)
        courseRepository.save(newCourse)
        def newCE = new CourseExecution(newCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(newCE)
        return newCE
    }

    def newstudent(courseExecution, username) {
        def student = new Student(USER_1_NAME, username, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student.addCourse(courseExecution)
        userRepository.save(student)
        def studentDashboard = new StudentDashboard(courseExecution, student)
        studentDashboardRepository.save(studentDashboard)
        return student
    }

    def newQuestionStats(ce, td) {
        def questionStats = new QuestionStats(ce, td)
        questionStatsRepository.save(questionStats)
        return questionStats
    }


    def newQuestionSubmission(courseExecution, student, question) {
        def questionSubmission = new QuestionSubmission(courseExecution, question, student)
        questionSubmissionRepository.save(questionSubmission)
        return questionSubmission
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

    def answerQuiz(answered, correct, completed, quizQuestion, quiz, student, date = DateHandler.now()) {
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

    def quizz(student, answered, correct) {
        def question = createQuestion()
        def quiz = createQuiz()
        def quizQuestion = createQuizQuestion(quiz, question)
        def quizzAnswer = answerQuiz(answered, correct, true, quizQuestion, quiz, student)
        student.getCourseExecutionDashboard(externalCourseExecution).statistics(quizzAnswer)
    }


    //mudar o spocktest.groovy
    @Unroll
    def "create an empty QuestionStats"() {
        when: "a questionStats is created"
        def questionStats = newQuestionStats(externalCourseExecution, teacherDashboard)
        //QuestionStatsRepository.save(questionStats)
        then: "an empty dashboard is created"
        //QuestionStatsRepository.count() == 1L
        def result = questionStats//studentStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getNumQuestionsAvailable() == 0
        result.getNumQuestionsAnsweredUniq() == 0
        result.getAverageQuestionsAnsweredUniq() == 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacherDashboard().getId() == teacherDashboard.getId()

        and: "the teacherDashboard has a reference for the QuestionStats"
        teacherDashboard.getQuestionStats().size() == 1
        teacherDashboard.getQuestionStats().contains(result)
    }

    @Unroll
    def "create an empty QuestionStats and remove it"() {
        when: "a QuestionStats is created"
        def questionStats = newQuestionStats(externalCourseExecution, teacherDashboard)

        then: "an empty questionStats is created and deleted"
        //QestionStatsRepository.count() == 1L
        def result = questionStats//studentStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacherDashboard().getId() == teacherDashboard.getId()
        result.remove()
        and: "the teacherDashboard has no reference for the questionStats"
        teacherDashboard.getQuestionStats().size() == 0
    }

    @Unroll
    def "create an empty QuestionStats and updated with course that as 1 empty question"() {
        when: "a questionStats is created"
        def questionStats = newQuestionStats(externalCourseExecution, teacherDashboard)

        def student1 = newstudent(externalCourseExecution,"1")
        def student2 = newstudent(externalCourseExecution,"2")

        externalCourseExecution.addUser(student1)
        externalCourseExecution.addUser(student2)

       	quizz(student1,true,false)
       	quizz(student2,false,false)

        then: "an empty questionStats is created and updated course that as 1 empty question"

        def result = questionStats//QuestionStatsRepository.findAll().get(0)

        result.update()
        result.getNumQuestionsAvailable() == 2
        result.getNumQuestionsAnsweredUniq() == 2
        result.getAverageQuestionsAnsweredUniq() == 1
    }

    @Unroll
    def "create an empty QuestionStats and updated with course that as 1 question but question does not have it"() {
        when: "a QuestionStats is created and a studentDashboard"
        def questionStats = newQuestionStats(externalCourseExecution, teacherDashboard)

		then: ""
		def result = questionStats
        questionStats.update()
        result.getNumQuestionsAvailable() == 0
        result.getNumQuestionsAnsweredUniq() == 0
        result.getAverageQuestionsAnsweredUniq() == 0
    }

    @Unroll
    def "create empty QuestionStats and use toString"() {
        when: "a QuestionStats is created"

        def newTeacher = new Teacher(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        userRepository.save(newTeacher)

        def td = new TeacherDashboard(externalCourseExecution, newTeacher)
        teacherDashboardRepository.save(td)
        newQuestionStats(externalCourseExecution, td)

        then: "compare toString"
        questionStatsRepository.count() == 1L
        def result = questionStatsRepository.findAll().get(0)
        result.toString()==td.getCourseExecutionQuestionStats(externalCourseExecution).toString()
    }

    @Unroll
    def "create an empty StudentStats and get it from teacherdashboard with the course"() {
        when: "a studentStats is created"
        newQuestionStats(externalCourseExecution, teacherDashboard)
        def newCE = newCourseExecution("123")

        then: "an empty studentStats is created and get from teacherdashboard with the course"
        questionStatsRepository.count() == 1L
        def result = questionStatsRepository.findAll().get(0)
        teacherDashboard.getCourseExecutionQuestionStats(externalCourseExecution)==result
        teacherDashboard.getCourseExecutionQuestionStats(newCE)==null
    }

    @Unroll
    def "create an empty QuestionStats and add it 2 times to teacherDashboard"() {
        when: "a questionStats is created"
        newQuestionStats(externalCourseExecution, teacherDashboard)

        then: "try to add the StudenStats again"
        questionStatsRepository.count() == 1L
        def result = questionStatsRepository.findAll().get(0)
        def c=0;

        try {
            teacherDashboard.addQuestionStats(result)
        }
        catch(Exception QUESTION_STAT_ALREADY_EXISTS) {
            c=1
        }
        c==1
    }

    @Unroll
    def "create 2 empty QuestionStats and add it 2 times to teacherDashboard"() {
        when: "a questionStats is created"
        newQuestionStats(externalCourseExecution, teacherDashboard)
        newQuestionStats(externalCourseExecution, teacherDashboard)

        then: "try to add the StudenStats again"
        questionStatsRepository.count() == 2L
    }

    @Unroll
    def "create an empty QuestionStats and update it from teacherDashboard"() {
        when: "a questionStats is created and the quizzes"
        newQuestionStats(externalCourseExecution, teacherDashboard)
        def student1 = newstudent(externalCourseExecution,"1")
        def student2 = newstudent(externalCourseExecution,"2")
        newstudent(externalCourseExecution,"3")

        for(int i=0;i<3;i++) {
            for(int j=0;j<4;j++){
                quizz(student1,true,false)
            }
            quizz(student1,true,true)
            quizz(student2,true,true)
        }

        then: "an empty studentStats is updated"
        questionStatsRepository.count() == 1L
        def result = questionStatsRepository.findAll().get(0)

        teacherDashboard.update()
        result.getNumQuestionsAvailable() == 18
        result.getNumQuestionsAnsweredUniq() == 18
        result.getAverageQuestionsAnsweredUniq() == 6
    }



    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}

}

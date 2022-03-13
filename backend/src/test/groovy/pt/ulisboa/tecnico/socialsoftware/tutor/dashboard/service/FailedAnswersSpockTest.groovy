package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.MultipleChoiceStatementAnswerDetailsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.FailedAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.SameQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

import java.time.LocalDateTime

class FailedAnswersSpockTest extends SpockTest {

    def dashboard
    def student
    def question
    def option
    def optionKO

    def createQuiz(count, type = Quiz.QuizType.PROPOSED.toString()) {
        def quiz = new Quiz()
        quiz.setKey(count)
        quiz.setTitle("Quiz Title")
        quiz.setType(type)
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setCreationDate(DateHandler.now())
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)
        return quiz
    }

    def createQuestion(count, quiz) {
        question = new Question()
        question.setKey(count)
        question.setTitle("Question Title")
        question.setCourse(externalCourse)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionRepository.save(question)

        option = new Option()
        option.setContent("Option Content")
        option.setCorrect(true)
        option.setSequence(0)
        option.setQuestionDetails(questionDetails)
        optionRepository.save(option)
        optionKO = new Option()
        optionKO.setContent("Option Content")
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)

        def quizQuestion = new QuizQuestion(quiz, question, 0)
        quizQuestionRepository.save(quizQuestion)
        return quizQuestion
    }

    def addExistingQuestionToQuiz(quiz, question=question) {
        def quizQuestion = new QuizQuestion(quiz, question, 0)
        quizQuestionRepository.save(quizQuestion)
        return quizQuestion
    }

    def answerQuiz(answered, correct, completed, question, quiz, date = LocalDateTime.now()) {
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
        questionAnswer.setQuizQuestion(question)

        def answerDetails
        if (answered && correct) answerDetails = new MultipleChoiceAnswer(questionAnswer, optionRepository.findAll().get(0))
        else if (answered && !correct ) answerDetails = new MultipleChoiceAnswer(questionAnswer, optionRepository.findAll().get(1))
        else {
            questionAnswerRepository.save(questionAnswer)
            return questionAnswer
        }
        questionAnswer.setAnswerDetails(answerDetails)
        answerDetailsRepository.save(answerDetails)
        questionAnswerRepository.save(questionAnswer)
        return questionAnswer
    }

    def createFailedAnswer(questionAnswer, collected) {
        def failedAnswer = new FailedAnswer()
        failedAnswer.setQuestionAnswer(questionAnswer)
        failedAnswer.setAnswered(questionAnswer.isAnswered())
        failedAnswer.setCollected(collected)
        failedAnswer.setDashboard(dashboard)
        SameQuestion sameQuestion = new SameQuestion(failedAnswer);
        failedAnswer.setSameQuestion(sameQuestion);
        failedAnswerRepository.save(failedAnswer)

        return failedAnswer
    }

    def answerQuizIT(answered, correct, quiz, student=student) {
        def quizAnswer = new QuizAnswer(student, quiz)
        quizAnswer.setCreationDate(quiz.getCreationDate())
        quizAnswerRepository.save(quizAnswer)

        def statementQuizDto = new StatementQuizDto()
        statementQuizDto.id = quiz.getId()
        statementQuizDto.quizAnswerId = quizAnswer.getId()

        def statementAnswerDto = new StatementAnswerDto()
        def multipleChoiceAnswerDto = new MultipleChoiceStatementAnswerDetailsDto()

        if (answered && correct) multipleChoiceAnswerDto.setOptionId(option.getId())
        else if (answered && !correct ) multipleChoiceAnswerDto.setOptionId(optionKO.getId())

        statementAnswerDto.setAnswerDetails(multipleChoiceAnswerDto)
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        statementAnswerDto.setQuestionAnswerId(quizAnswer.getQuestionAnswers().get(0).getId())
        statementQuizDto.getAnswers().add(statementAnswerDto)

        answerService.concludeQuiz(statementQuizDto)

        return quizAnswer.getQuestionAnswers().get(0)
    }
}

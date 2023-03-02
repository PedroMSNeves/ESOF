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

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course

import spock.lang.Unroll

@DataJpaTest
class QuestionStatsTest extends SpockTest {
    def teacherDashboard
    def teacher

    def setup() {
        createExternalCourseAndExecution()
        teacher = new Teacher(USER_1_NAME, false)
        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)
    }
    
    def newCourseExecution()
    {
        def newCourse = new Course("123", Course.Type.TECNICO)
        courseRepository.save(newCourse)
        def newCE  = new CourseExecution(newCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(newCE)
        return newCE
    }
    
    def newstudent(courseExecution,username){
        def student = new Student(USER_1_NAME, username, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student.addCourse(courseExecution)
        userRepository.save(student)
        def studentDashboard = new StudentDashboard(courseExecution, student)
        studentDashboardRepository.save(studentDashboard)
        return student
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

        return newQuestion
    }
    
    def newQuestionStats(ce, td) {
   		def questionstats = new QuestionStats(ce, td)
   		questionStatsRepository.save(questionstats)
   		td.addQuestionStats(studentstats)
   		return questionstats
    }

    
    def newQuestionSubmission(courseExecution, student, question){
        def QuestionSubmission = new QuestionSubmission(question,student,coursExecution,IN_REVIEW, false, false, [])
        questionRepository.save(QuestionSubmission)
        return QuestionSubmission
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
        teacherDashboard.remove()
        and: "the teacherDashboard has no reference for the questionStats"
        teacherDashboard.getStudentStats().size() == 0
    }

    @Unroll
    def "create an empty QuestionStats and updated with course that as 1 empty question"() {
        when: "a questionStats is created"
        def questionStats = newQuestionStats(externalCourseExecution, teacherDashboard)
        
        def student1 = newstudent(externalCourseExecution,"1")
        externalCourseExecution.addUser(student1)
        def question = createQuestion()
        def question_submission = newQuestionSubmission(externalCourseExecution, student1, question)
        
        then: "an empty questionStats is created and updated course that as 1 empty question"
        externalCourseExecution.addQuestionSubmission(question_submission)
        def result = questionStats//QuestionStatsRepository.findAll().get(0)

        result.update()
        result.getNumQuestionsAvailable() == 1
        result.getNumQuestionsAnsweredUniq() == 1
        result.getAverageQuestionsAnsweredUniq() == 1
    }

    @Unroll
    def "create an empty QuestionStats and updated with course that as 1 question but question does not have it"() {
        when: "a QuestionStats is created and a studentDashboard"
        def questionStats = newQuestionStats(externalCourseExecution, teacherDashboard)

        def qst = new Question(externalCourseExecution, student)
        student.addCourse(externalCourseExecution)
        def newCourse = new Course(COURSE_1_NAME, Course.Type.TECNICO)
        def newCourseExecution = new CourseExecution(newCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)

        def studentdashboard = new StudentDashboard(newCourseExecution, student)
        studentDashboardRepository.save(studentdashboard)
        then: "an empty questionStats is created and updated course that as 1 question but question does not have it"
        //studentStatsRepository.count() == 1L
        def result = questionStats//QuestionStatsRepository.findAll().get(0)

        QuestionStats.update()
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
        newQuestionStats(td,externalCourseExecution)

        then: "compare toString"
        questionStatsRepository.count() == 1L
        def result = questionStatsRepository.findAll().get(0)
        result.toString()==td.getCourseExecutionStudentStats(externalCourseExecution).toString()
    }
	


}
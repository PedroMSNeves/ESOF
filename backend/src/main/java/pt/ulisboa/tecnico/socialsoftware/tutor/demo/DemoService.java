package pt.ulisboa.tecnico.socialsoftware.tutor.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerItemRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.DifficultQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.repository.StudentDashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.repository.DifficultQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.DiscussionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.TopicConjunction;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.AssessmentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.repository.QuestionSubmissionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.dto.AuthDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.AuthUserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.StudentStatsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.QuizStatsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.QuestionStatsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.TeacherDashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher;
import java.util.Collections;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DemoService {
    List<Integer> questions2Keep = Arrays.asList(1320, 1940, 1544, 11081, 11082);

    List<Integer> questionsInQuizzes = Arrays.asList(1940, 11081, 11082);

    Integer quiz2Keep = 40438;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private CourseExecutionService courseExecutionService;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionAnswerItemRepository questionAnswerItemRepository;

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private QuestionSubmissionRepository questionSubmissionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentDashboardRepository studentDashboardRepository;

    @Autowired
    private DifficultQuestionRepository difficultQuestionRepository;

    @Autowired
    private AuthUserService authUserService;

    @Autowired
    private StudentStatsRepository studentStatsRepository;

    @Autowired
    private QuizStatsRepository quizStatsRepository;

    @Autowired
    private QuestionStatsRepository questionStatsRepository;

    @Autowired
    private TeacherDashboardRepository teacherDashboardRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoDashboards() {
        userRepository.findAll()
                .stream()
                .filter(user -> user.getAuthUser() != null && user.getAuthUser().isDemoStudent())
                .map(Student.class::cast)
                .flatMap(student -> student.getDashboards().stream())
                .forEach(dashboard -> {
                    dashboard.remove();
                    this.studentDashboardRepository.delete(dashboard);
                });

        Set<DifficultQuestion> difficultQuestionsToRemove = courseExecutionRepository.findById(courseExecutionService.getDemoCourse().getCourseExecutionId()).stream()
                .flatMap(courseExecution -> courseExecution.getDifficultQuestions().stream())
                .collect(Collectors.toSet());

        difficultQuestionsToRemove.forEach(difficultQuestion -> {
            difficultQuestion.remove();
            difficultQuestionRepository.delete(difficultQuestion);
        });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoAssessments() {
        Integer courseExecutionId = courseExecutionService.getDemoCourse().getCourseExecutionId();

        this.assessmentRepository.findByExecutionCourseId(courseExecutionId)
                .stream()
                .forEach(assessment -> {
                    assessment.remove();
                    assessmentRepository.delete(assessment);
                });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoTopics() {
        Integer courseId = courseExecutionService.getDemoCourse().getCourseId();

        this.topicRepository.findTopics(courseId)
                .stream()
                .forEach(topic -> {
                    topic.remove();
                    this.topicRepository.delete(topic);
                });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoDiscussions() {
        List<Discussion> discussions = discussionRepository.findByExecutionCourseId(courseExecutionService.getDemoCourse().getCourseExecutionId());

        discussions.forEach(discussion -> {
            discussion.remove();
            discussionRepository.delete(discussion);
        });
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoAnswers() {
        Set<QuestionAnswerItem> questionAnswerItems = questionAnswerItemRepository.findDemoStudentQuestionAnswerItems();
        questionAnswerItemRepository.deleteAll(questionAnswerItems);

        Set<QuizAnswer> quizAnswers = quizAnswerRepository.findByExecutionCourseId(courseExecutionService.getDemoCourse().getCourseExecutionId());

        for (QuizAnswer quizAnswer : quizAnswers) {
            if (!quizAnswer.getQuiz().getId().equals(quiz2Keep) || !quizAnswer.getStudent().getUsername().equals(DemoUtils.STUDENT_USERNAME)) {
                quizAnswer.remove();
                quizAnswerRepository.delete(quizAnswer);
            }
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoTournaments() {
        tournamentRepository.getTournamentsForCourseExecution(courseExecutionService.getDemoCourse().getCourseExecutionId())
                .forEach(tournament -> {
                    tournament.getParticipants().forEach(user -> user.removeTournament(tournament));
                    if (tournament.getQuiz() != null) {
                        tournament.getQuiz().setTournament(null);
                    }

                    tournamentRepository.delete(tournament);
                });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoQuizzes() {
        // remove quizzes except to keep
        quizRepository.findQuizzesOfExecution(courseExecutionService.getDemoCourse().getCourseExecutionId())
                .stream()
                .forEach(quiz -> {
                    if (!quiz.getId().equals(quiz2Keep)) {
                        quiz.remove();
                        this.quizRepository.delete(quiz);
                    }
                });

        // remove questions except to keep and that are not submitted
        for (Question question : questionRepository.findQuestions(courseExecutionService.getDemoCourse().getCourseId())
                .stream()
                .filter(question -> !questions2Keep.contains(question.getId()) && questionSubmissionRepository.findQuestionSubmissionByQuestionId(question.getId()) == null)
                .collect(Collectors.toList())) {
            questionService.removeQuestion(question.getId());
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoQuestionSubmissions() {
        questionSubmissionRepository.findQuestionSubmissionsByCourseExecution(courseExecutionService.getDemoCourse().getCourseExecutionId())
                .forEach(questionSubmission -> {
                    questionSubmission.remove();
                    questionSubmissionRepository.delete(questionSubmission);
                });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoStudents() {
        userRepository.findAll()
                .stream()
                .filter(user -> user.getAuthUser() != null && user.getAuthUser().isDemoStudent())
                .map(Student.class::cast)
                .forEach(student -> {
                    if (student.getQuizAnswers().isEmpty()) {
                        student.remove();
                        this.userRepository.delete(student);
                    }
                });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void populateDemo() {
        Integer courseId = courseExecutionService.getDemoCourse().getCourseId();
        Integer courseExecutionId = courseExecutionService.getDemoCourse().getCourseExecutionId();

        Topic softwareArchitectureTopic = new Topic();
        softwareArchitectureTopic.setName("Software Architecture");
        softwareArchitectureTopic.setCourse(courseRepository.findById(courseId).orElseThrow(() -> new TutorException(ErrorMessage.COURSE_NOT_FOUND)));
        topicRepository.save(softwareArchitectureTopic);

        Topic softwareEngineeringTopic = new Topic();
        softwareEngineeringTopic.setName("Software Engineering");
        softwareEngineeringTopic.setCourse(courseRepository.findById(courseId).orElseThrow(() -> new TutorException(ErrorMessage.COURSE_NOT_FOUND)));
        topicRepository.save(softwareEngineeringTopic);

        List<Question> questions = questionRepository.findQuestions(courseId);
        questions.forEach(question -> {
            question.setStatus(Question.Status.AVAILABLE);
            question.addTopic(softwareEngineeringTopic);
        });

        Assessment assessment = new Assessment();
        assessment.setTitle("Software Engineering Questions");
        assessment.setStatus(Assessment.Status.AVAILABLE);
        assessment.setSequence(1);
        assessment.setCourseExecution(courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_FOUND)));
        TopicConjunction topicConjunction = new TopicConjunction();
        topicConjunction.addTopic(softwareEngineeringTopic);
        topicConjunction.setAssessment(assessment);
        assessmentRepository.save(assessment);

        Quiz inClassOneWayQuiz = new Quiz();
        inClassOneWayQuiz.setTitle("In Class Quiz One Way");
        inClassOneWayQuiz.setType(Quiz.QuizType.IN_CLASS.name());
        inClassOneWayQuiz.setCreationDate(DateHandler.now());
        inClassOneWayQuiz.setAvailableDate(DateHandler.now());
        inClassOneWayQuiz.setConclusionDate(DateHandler.now().plusHours(22));
        inClassOneWayQuiz.setCourseExecution(courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_FOUND)));
        inClassOneWayQuiz.setOneWay(true);
        inClassOneWayQuiz.setScramble(true);

        Quiz inClassQuiz = new Quiz();
        inClassQuiz.setTitle("In Class Quiz");
        inClassQuiz.setType(Quiz.QuizType.IN_CLASS.name());
        inClassQuiz.setCreationDate(DateHandler.now());
        inClassQuiz.setAvailableDate(DateHandler.now());
        inClassQuiz.setConclusionDate(DateHandler.now().plusHours(22));
        inClassQuiz.setCourseExecution(courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_FOUND)));
        inClassQuiz.setScramble(true);

        Quiz proposedQuiz = new Quiz();
        proposedQuiz.setTitle("Teacher Proposed");
        proposedQuiz.setType(Quiz.QuizType.PROPOSED.name());
        proposedQuiz.setCreationDate(DateHandler.now());
        proposedQuiz.setAvailableDate(DateHandler.now());
        proposedQuiz.setCourseExecution(courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_FOUND)));
        proposedQuiz.setScramble(true);

        Quiz scrambledQuiz = new Quiz();
        scrambledQuiz.setTitle("Non Scrambled");
        scrambledQuiz.setType(Quiz.QuizType.PROPOSED.name());
        scrambledQuiz.setCreationDate(DateHandler.now());
        scrambledQuiz.setAvailableDate(DateHandler.now());
        scrambledQuiz.setCourseExecution(courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_FOUND)));

        questions.forEach(question -> {
            if (questionsInQuizzes.contains(question.getId())) {
                new QuizQuestion(inClassOneWayQuiz, question, inClassOneWayQuiz.getQuizQuestionsNumber());
                new QuizQuestion(inClassQuiz, question, inClassQuiz.getQuizQuestionsNumber());
                new QuizQuestion(proposedQuiz, question, proposedQuiz.getQuizQuestionsNumber());
                new QuizQuestion(scrambledQuiz, question, scrambledQuiz.getQuizQuestionsNumber());
            }
        });

        quizRepository.save(inClassOneWayQuiz);
        quizRepository.save(inClassQuiz);
        quizRepository.save(proposedQuiz);
        quizRepository.save(scrambledQuiz);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void NewPopulateDemo() {
        Integer courseId = courseExecutionService.getDemoCourse().getCourseId();
        Integer courseExecutionId = courseExecutionService.getDemoCourse().getCourseExecutionId();

        // Let's update the end date of the demo execution
        CourseExecution demoExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_FOUND));
        demoExecution.setEndDate(DateHandler.toLocalDateTime("2017-12-31T10:15:30+01:00[Europe/Lisbon]"));

        // Simulate login of demo teacher (this adds the demo teacher to the original demo execution
        AuthDto authDemoTeacherDto = authUserService.demoTeacherAuth();

        // Get demo course and demo teacher
        Course demoCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new TutorException(ErrorMessage.COURSE_NOT_FOUND));
        User demoTeacher = userRepository.findById(authDemoTeacherDto.getUser().getId())
                .orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND));

        // Create three additional course executions
        List<String> endDates = Arrays.asList(
                "2019-12-31T10:15:30+01:00[Europe/Lisbon]",
                "2022-12-31T10:15:30+01:00[Europe/Lisbon]",
                "2023-12-31T10:15:30+01:00[Europe/Lisbon]"
        );
        List<String> academicTerms = Arrays.asList("2st semester 2019/2020", "2st semester 2022/2023", "2st semester 2023/2024");

        StudentStats st1_1 = new StudentStats();
        st1_1.setNumStudents(5);
        st1_1.setNumMore75CorrectQuestions(0);
        st1_1.setNumAtLeast3Quizzes(2);

        StudentStats st2_1 = new StudentStats();
        st2_1.setNumStudents(5);
        st2_1.setNumMore75CorrectQuestions(0);
        st2_1.setNumAtLeast3Quizzes(2);

        StudentStats st2_2 = new StudentStats();
        st2_2.setNumStudents(7);
        st2_2.setNumMore75CorrectQuestions(1);
        st2_2.setNumAtLeast3Quizzes(3);

        StudentStats st3_1 = new StudentStats();
        st3_1.setNumStudents(5);
        st3_1.setNumMore75CorrectQuestions(0);
        st3_1.setNumAtLeast3Quizzes(2);

        StudentStats st3_2 = new StudentStats();
        st3_2.setNumStudents(7);
        st3_2.setNumMore75CorrectQuestions(1);
        st3_2.setNumAtLeast3Quizzes(3);

        StudentStats st3_3 = new StudentStats();
        st3_3.setNumStudents(21);
        st3_3.setNumMore75CorrectQuestions(10);
        st3_3.setNumAtLeast3Quizzes(21);



        QuizStats quiz1_1 = new QuizStats();
        quiz1_1.setNumQuizzes(5);
        quiz1_1.setNumUniqueAnsweredQuizzes(0);
        quiz1_1.setAverageQuizzesSolved(2);

        QuizStats quiz2_1 = new QuizStats();
        quiz2_1.setNumQuizzes(5);
        quiz2_1.setNumUniqueAnsweredQuizzes(0);
        quiz2_1.setAverageQuizzesSolved(2);

        QuizStats quiz2_2 = new QuizStats();
        quiz2_2.setNumQuizzes(7);
        quiz2_2.setNumUniqueAnsweredQuizzes(1);
        quiz2_2.setAverageQuizzesSolved(3);

        QuizStats quiz3_1 = new QuizStats();
        quiz3_1.setNumQuizzes(5);
        quiz3_1.setNumUniqueAnsweredQuizzes(0);
        quiz3_1.setAverageQuizzesSolved(2);

        QuizStats quiz3_2 = new QuizStats();
        quiz3_2.setNumQuizzes(7);
        quiz3_2.setNumUniqueAnsweredQuizzes(1);
        quiz3_2.setAverageQuizzesSolved(3);

        QuizStats quiz3_3 = new QuizStats();
        quiz3_3.setNumQuizzes(21);
        quiz3_3.setNumUniqueAnsweredQuizzes(10);
        quiz3_3.setAverageQuizzesSolved(21);

        QuestionStats quest1_1 = new QuestionStats();
        quest1_1.setNumAvailable(5);
        quest1_1.setAnsweredQuestionsUnique(0);
        quest1_1.setAverageQuestionsAnswered(2.9f);

        QuestionStats quest2_1 = new QuestionStats();
        quest2_1.setNumAvailable(5);
        quest2_1.setAnsweredQuestionsUnique(0);
        quest2_1.setAverageQuestionsAnswered(2.9f);

        QuestionStats quest2_2 = new QuestionStats();
        quest2_2.setNumAvailable(7);
        quest2_2.setAnsweredQuestionsUnique(1);
        quest2_2.setAverageQuestionsAnswered(3.2f);

        QuestionStats quest3_1 = new QuestionStats();
        quest3_1.setNumAvailable(5);
        quest3_1.setAnsweredQuestionsUnique(0);
        quest3_1.setAverageQuestionsAnswered(2.9f);

        QuestionStats quest3_2 = new QuestionStats();
        quest3_2.setNumAvailable(7);
        quest3_2.setAnsweredQuestionsUnique(1);
        quest3_2.setAverageQuestionsAnswered(3.2f);

        QuestionStats quest3_3 = new QuestionStats();
        quest3_3.setNumAvailable(21);
        quest3_3.setAnsweredQuestionsUnique(10);
        quest3_3.setAverageQuestionsAnswered(21.0f);

        for (int i = 0; i < endDates.size(); i++) {
            CourseExecution newCE = new CourseExecution(
                    demoCourse,
                    "Demo Course",
                    academicTerms.get(i),
                    Course.Type.TECNICO,
                    DateHandler.toLocalDateTime(endDates.get(i)));

            demoTeacher.addCourse(newCE);

            courseExecutionRepository.save(newCE);

            TeacherDashboard teacherDashboard = new TeacherDashboard(newCE,(Teacher)demoTeacher);

            if(i==0){
                st3_1.setCourseExecution(newCE);
                quest3_1.setCourseExecution(newCE);
                quiz3_1.setCourseExecution(newCE);
                st2_1.setCourseExecution(newCE);
                quest2_1.setCourseExecution(newCE);
                quiz2_1.setCourseExecution(newCE);

                st1_1.setCourseExecution(newCE);
                quest1_1.setCourseExecution(newCE);
                quiz1_1.setCourseExecution(newCE);
                st1_1.setTeacherDashboard(teacherDashboard);
                quest1_1.setDashboard(teacherDashboard);
                quiz1_1.setTeacherDashboard(teacherDashboard);

                studentStatsRepository.save(st1_1);
                quizStatsRepository.save(quiz1_1);
                questionStatsRepository.save(quest1_1);

            }
            if(i==1){
                st3_2.setCourseExecution(newCE);
                quest3_2.setCourseExecution(newCE);
                quiz3_2.setCourseExecution(newCE);

                st2_2.setCourseExecution(newCE);
                quest2_2.setCourseExecution(newCE);
                quiz2_2.setCourseExecution(newCE);
                st2_2.setTeacherDashboard(teacherDashboard);
                quest2_2.setDashboard(teacherDashboard);
                quiz2_2.setTeacherDashboard(teacherDashboard);


                st2_1.setTeacherDashboard(teacherDashboard);
                quest2_1.setDashboard(teacherDashboard);
                quiz2_1.setTeacherDashboard(teacherDashboard);

                studentStatsRepository.save(st2_2);
                quizStatsRepository.save(quiz2_2);
                questionStatsRepository.save(quest2_2);
                studentStatsRepository.save(st2_1);
                quizStatsRepository.save(quiz2_1);
                questionStatsRepository.save(quest2_1);
            }
            if(i==2){
                st3_3.setCourseExecution(newCE);
                quest3_3.setCourseExecution(newCE);
                quiz3_3.setCourseExecution(newCE);
                st3_3.setTeacherDashboard(teacherDashboard);
                quest3_3.setDashboard(teacherDashboard);
                quiz3_3.setTeacherDashboard(teacherDashboard);


                st3_2.setTeacherDashboard(teacherDashboard);
                quest3_2.setDashboard(teacherDashboard);
                quiz3_2.setTeacherDashboard(teacherDashboard);

                st3_1.setTeacherDashboard(teacherDashboard);
                quest3_1.setDashboard(teacherDashboard);
                quiz3_1.setTeacherDashboard(teacherDashboard);

                studentStatsRepository.save(st3_3);
                quizStatsRepository.save(quiz3_3);
                questionStatsRepository.save(quest3_3);
                studentStatsRepository.save(st3_2);
                quizStatsRepository.save(quiz3_2);
                questionStatsRepository.save(quest3_2);
                studentStatsRepository.save(st3_1);
                quizStatsRepository.save(quiz3_1);
                questionStatsRepository.save(quest3_1);


            }

            teacherDashboardRepository.save(teacherDashboard);
        }

        CourseExecution newCE = new CourseExecution(
                demoCourse,
                "Demo Course",
                "2st semester 2000/2001",
                Course.Type.TECNICO,
                DateHandler.toLocalDateTime( "2001-12-31T10:15:30+01:00[Europe/Lisbon]"));

        demoTeacher.addCourse(newCE);
        courseExecutionRepository.save(newCE);
        userRepository.save(demoTeacher);
    }
}
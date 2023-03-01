package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.TeacherDashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.QuestionStatsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto.QuestionStatsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.Stats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;

import java.util.*;
import java.util.stream.Collectors;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;


public class QuestionStatsService {
}

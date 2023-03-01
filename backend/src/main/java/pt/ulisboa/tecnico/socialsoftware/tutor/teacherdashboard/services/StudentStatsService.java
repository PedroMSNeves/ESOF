/*package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.TeacherDashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.StudentStatsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto.StudentStatsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;

import java.util.*;
import java.util.stream.Collectors;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class StudentStatsService {

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;
    @Autowired
    private TeacherDashboardRepository teacherDashboardRepository;
    @Autowired
    private StudentStatsRepository studentStatsRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<StudentStatsDto> updateStudentStats(Integer dashboardId) {
        if (dashboardId == null) {
            throw new TutorException(DASHBOARD_NOT_FOUND);
        }

        TeacherDashboard teacherDashboard = teacherDashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new TutorException(DASHBOARD_NOT_FOUND, dashboardId)); //esta excecao tambem da para teacher?

        Set<StudentStats> studentStats = teacherDashboard.getStudentStats();
        //acho que nunca fica null

        //fazer verificacao das disciplinas
        //createMissingWeeklyScores(studentDashboard, now); verifica e muda lista de studentstats


        //fazer add update as cenas
        update(teacherDashboard);

        return studentStats.stream()
                .sorted(Comparator.comparing(StudentStats::getEndDate, Comparator.reverseOrder()))
                .map(StudentStatsDto::new)
                .collect(Collectors.toList());
    }
    private void update(TeacherDashboard teacherDashboard) {
        teacherDashboard.getStudentStats().stream()
                .forEach(studentStat -> {
                    studentStat.update();//ness dar save() ou nao (int vs Integer)
                });
    }
}*/


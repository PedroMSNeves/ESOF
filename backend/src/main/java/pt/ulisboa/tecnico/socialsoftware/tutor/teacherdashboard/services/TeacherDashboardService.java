package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto.TeacherDashboardDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.TeacherDashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.TeacherRepository;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.StudentStatsRepository;
import java.time.LocalDateTime;

import java.util.*;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class TeacherDashboardService {

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private TeacherDashboardRepository teacherDashboardRepository;

    @Autowired
    private StudentStatsRepository studentStatsRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TeacherDashboardDto getTeacherDashboard(int courseExecutionId, int teacherId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, teacherId));

        if (!teacher.getCourseExecutions().contains(courseExecution))
            throw new TutorException(TEACHER_NO_COURSE_EXECUTION);

        Optional<TeacherDashboard> dashboardOptional = teacher.getDashboards().stream()
                .filter(dashboard -> dashboard.getCourseExecution().getId().equals(courseExecutionId))
                .findAny();

        return dashboardOptional.
                map(TeacherDashboardDto::new).
                orElseGet(() -> createAndReturnTeacherDashboardDto(courseExecution, teacher));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TeacherDashboardDto createTeacherDashboard(int courseExecutionId, int teacherId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, teacherId));

        if (teacher.getDashboards().stream().anyMatch(dashboard -> dashboard.getCourseExecution().equals(courseExecution)))
            throw new TutorException(TEACHER_ALREADY_HAS_DASHBOARD);

        if (!teacher.getCourseExecutions().contains(courseExecution))
            throw new TutorException(TEACHER_NO_COURSE_EXECUTION);

        return createAndReturnTeacherDashboardDto(courseExecution, teacher);
    }

    private TeacherDashboardDto createAndReturnTeacherDashboardDto(CourseExecution courseExecution, Teacher teacher) {
        TeacherDashboard teacherDashboard = new TeacherDashboard(courseExecution, teacher);//ir buscar os 3 ultimos
        this.addLast3Executions(teacherDashboard);
        teacherDashboardRepository.save(teacherDashboard);

        return new TeacherDashboardDto(teacherDashboard);
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateAllTeacherDashboards() {
        Iterable<Teacher> teacher = teacherRepository.findAll();
        teacher.forEach(t -> {
            t.getCourseExecutions().forEach(ex -> {
                if (!t.getDashboards().stream().anyMatch(dashboard -> dashboard.getCourseExecution().equals(ex)))
                    createAndReturnTeacherDashboardDto(ex,t);
                t.getCourseExecutionDashboard(ex).update();
                teacherDashboardRepository.save(t.getCourseExecutionDashboard(ex));
            });
            teacherRepository.save(t);
        });
    }



    private void addLast3Executions(TeacherDashboard teacherDashboard) {
        // a ce que estamos a usar e a mais recente? estamos a assumir que sim
            teacherDashboard.getCourseExecution().getCourse().getCourseExecutions().stream()
                    .sorted(Comparator.comparing(CourseExecution::getEndDate,Comparator.nullsFirst(Comparator.naturalOrder())).reversed()).limit(3).forEach(ce -> {
                        if(ce.getEndDate()!=null) {
                            StudentStats st = new StudentStats(ce, teacherDashboard);
                            studentStatsRepository.save(st);
                        }
                    });//testar
    }
    public void updateTeacherDashboard(int dashboardId) {
        TeacherDashboard teacherDashboard = teacherDashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(DASHBOARD_NOT_FOUND, dashboardId));
        teacherDashboard.update();
        teacherDashboardRepository.save(teacherDashboard);
    }

    public void updateAllTeacherDashboards() {
        Iterable<Teacher> teacher = teacherRepository.findAll();
        teacher.forEach(t -> {
            t.getCourseExecutions().forEach(ex -> {
                if (!t.getDashboards().stream().anyMatch(dashboard -> dashboard.getCourseExecution().equals(ex)))
                    createAndReturnTeacherDashboardDto(ex,t);
                t.getCourseExecutionDashboard(ex).update();
                teacherDashboardRepository.save(t.getCourseExecutionDashboard(ex));
            });
            teacherRepository.save(t);
        });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeTeacherDashboard(Integer dashboardId) {
        if (dashboardId == null)
            throw new TutorException(DASHBOARD_NOT_FOUND, -1);

        TeacherDashboard teacherDashboard = teacherDashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(DASHBOARD_NOT_FOUND, dashboardId));
        teacherDashboard.remove();
        teacherDashboardRepository.delete(teacherDashboard);
    }

}

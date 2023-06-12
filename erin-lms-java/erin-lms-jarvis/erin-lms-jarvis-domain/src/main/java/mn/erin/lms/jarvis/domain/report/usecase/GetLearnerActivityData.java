package mn.erin.lms.jarvis.domain.report.usecase;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.common.datetime.TimeUtils;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.domain.repository.CourseEnrollmentRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.jarvis.domain.report.model.analytics.UserActivityData;
import mn.erin.lms.jarvis.domain.report.service.EmployeeAnalyticsService;

public class GetLearnerActivityData {
    private final EmployeeAnalyticsService employeeAnalyticsService;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    protected CourseRepository courseRepository;
    public GetLearnerActivityData(EmployeeAnalyticsService employeeAnalyticsService,
                                  LmsRepositoryRegistry lmsRepositoryRegistry) {
        this.employeeAnalyticsService = Objects.requireNonNull(employeeAnalyticsService, "EmployeeAnalytics service cannot be null!");
        this.courseEnrollmentRepository = lmsRepositoryRegistry.getCourseEnrollmentRepository();
        this.courseRepository = lmsRepositoryRegistry.getCourseRepository();
    }
    public Object execute(List<String> users) {
        List<LearnerId> learners = users
                .stream().map(LearnerId::new).collect(Collectors.toList());

        Set<UserActivityData> userActivityData = employeeAnalyticsService.getUserActivityData(learners);

        if (userActivityData.isEmpty()) {
            return getDefaultOutput();
        }
        return toOutput(userActivityData, userActivityData.size());
    }

    private GetLearnerActivityDataOutput toOutput(Set<UserActivityData> userActivityData, int employeesCount) {
        long totalSpentTime = 0L;
        int perfectCompletionCountByEmployee = 0;
        for (UserActivityData datum : userActivityData) {
            List<CourseEnrollment> enrollments = courseEnrollmentRepository.listAll(datum.getLearnerId())
                    .stream().filter(courseEnrollment -> courseRepository.exists(courseEnrollment.getCourseId())).collect(Collectors.toList());
            if (datum.getOverallTime() != 0) {
                totalSpentTime += datum.getOverallTime() / enrollments.size();
            }
            if (datum.getAverageStatus() >= 100) {
                perfectCompletionCountByEmployee++;
            }
        }
        GetLearnerActivityDataOutput output = new GetLearnerActivityDataOutput();
        output.setPerfectEmployeeCount(perfectCompletionCountByEmployee);
        output.setEmployeeCount(employeesCount);
        output.setAverageTime(TimeUtils.convertToStringRepresentation(getAverageTime(totalSpentTime, userActivityData.size())));
        return output;
    }

    private Long getAverageTime(Long totalSpentTime, int userCount) {
        if (totalSpentTime == 0L) {
            return 0L;
        }
        return totalSpentTime / userCount;
    }

    private static GetLearnerActivityDataOutput getDefaultOutput() {
        GetLearnerActivityDataOutput defaultOutput = new GetLearnerActivityDataOutput();
        defaultOutput.setAverageTime("00:00:00");
        defaultOutput.setEmployeeCount(0);
        defaultOutput.setPerfectEmployeeCount(0);
        defaultOutput.setPerfectSupersCount(0);
        return defaultOutput;
    }
}

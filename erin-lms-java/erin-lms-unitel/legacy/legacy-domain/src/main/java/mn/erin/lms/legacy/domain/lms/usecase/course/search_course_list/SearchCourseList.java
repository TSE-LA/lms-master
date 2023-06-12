package mn.erin.lms.legacy.domain.lms.usecase.course.search_course_list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollmentState;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.service.CourseService;
import mn.erin.lms.legacy.domain.lms.service.EnrollmentStateService;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course_group.GetGroupCourseInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_group.GetGroupCourses;

import static mn.erin.lms.legacy.domain.lms.usecase.course.CourseUseCase.getCourseOutput;

/**
 * author Naranbaatar Avir.
 */
public class SearchCourseList implements UseCase<SearchCourseListInput, List<GetCourseOutput>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchCourseList.class);

    private static final String INPUT_CANNOT_BE_NULL = "Search input data cannot be null!";
    private static final String SEARCH_KEY_CANNOT_BE_BLANK = "Search key cannot be blank!";

    private final CourseService courseService;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final AccessIdentityManagement accessIdentityManagement;
    private final CourseAuditRepository courseAuditRepository;
    private EnrollmentStateService enrollmentStateService;

    private final List<String> groupCoursesIds;

    public SearchCourseList(EnrollmentStateService enrollmentStateService, CourseService courseService, CourseEnrollmentRepository courseEnrollmentRepository,
                            AccessIdentityManagement accessIdentityManagement, CourseAuditRepository courseAuditRepository, GetGroupCourses getGroupCourses) {
        groupCoursesIds = getGroupCourses.execute(new GetGroupCourseInput(null));
        this.courseService = Validate.notNull(courseService, "CourseService can not be null");
        this.enrollmentStateService = Objects.requireNonNull(enrollmentStateService, "EnrollmentStateService cannot be null!");
        this.courseEnrollmentRepository = courseEnrollmentRepository;
        this.accessIdentityManagement = accessIdentityManagement;
        this.courseAuditRepository = courseAuditRepository;
    }

    @Override
    public List<GetCourseOutput> execute(SearchCourseListInput input) throws UseCaseException {
        List<Course> courseList;
        if (null == input) {
            throw new UseCaseException(INPUT_CANNOT_BE_NULL);
        }

        if (StringUtils.isBlank(input.getSearchKey())) {
            throw new UseCaseException(SEARCH_KEY_CANNOT_BE_BLANK);
        }

        String searchKey = input.getSearchKey();
        try {
            courseList = courseService.search(searchKey, input.isSearchByName(), input.isSearchByDescription());
        } catch (LMSRepositoryException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UseCaseException("Failed to search courselist");
        }

        return this.createOutput(courseList);
    }

    private List<GetCourseOutput> createOutput(List<Course> courseList) {
        String currentUsername = accessIdentityManagement.getCurrentUsername();
        LearnerId learnerId = new LearnerId(currentUsername);

        List<String> enrolledCourseIds = courseEnrollmentRepository.getEnrollmentList(learnerId)
                .stream().map(courseEnrollment -> courseEnrollment.getCourseId().getId()).collect(Collectors.toList());

        List<GetCourseOutput> results = new ArrayList<>();

        if ("LMS_ADMIN".equalsIgnoreCase(accessIdentityManagement.getRole(currentUsername))) {
            if (groupCoursesIds == null || groupCoursesIds.isEmpty()) {
                return Collections.emptyList();
            }
            for (Course course : courseList) {
                if (this.groupCoursesIds.contains(course.getCourseId().getId())) {
                    GetCourseOutput output = getCourseOutput(course);
                    results.add(output);
                }
            }
        } else {
            Set<String> courseAudits = courseAuditRepository.listAll(learnerId)
                    .stream().map(courseAudit -> courseAudit.getCourseId().getId()).collect(Collectors.toSet());
            for (Course course : courseList) {
                GetCourseOutput output = getCourseOutput(course);
                if (enrolledCourseIds.contains(course.getCourseId().getId())) {
                    CourseEnrollmentState courseEnrollmentState = enrollmentStateService.getCourseEnrollmentState(course.getCourseId());
                    output.setEnrollmentState(courseEnrollmentState.name());
                    results.add(output);
                }
                if (courseAudits.contains(course.getCourseId().getId())) {
                    output.setEnrollmentState("AUDIT");
                    results.add(output);
                }
            }
        }
        return results;
    }
}

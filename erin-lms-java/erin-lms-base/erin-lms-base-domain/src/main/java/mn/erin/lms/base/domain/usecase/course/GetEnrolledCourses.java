package mn.erin.lms.base.domain.usecase.course;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.CourseType;
import mn.erin.lms.base.domain.model.course.PublishStatus;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.aim.user.Learner;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.base.domain.usecase.course.dto.GetCoursesInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Supervisor.class, Learner.class })
public class GetEnrolledCourses extends CourseUseCase<GetCoursesInput, List<CourseDto>>
{

  public GetEnrolledCourses(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  public List<CourseDto> execute(GetCoursesInput input) throws UseCaseException
  {
    LearnerId learnerId = LearnerId.valueOf(input.getLearnerId());
    List<CourseEnrollment> enrollments = courseEnrollmentRepository.listAll(learnerId);
    List<Course> courses = getCourses(input);

    if ("EMPLOYEE".equals(input.getRole()))
    {
      courses.removeIf(course ->
          course.getCourseDetail().getProperties().get("address") == null &&
              (course.getCourseType().getType().equals("MANAGER") ||
                  course.getCourseType().getType().equals("SUPERVISOR")));
    }

    if ("SUPERVISOR".equals(input.getRole()))
    {
      courses.removeIf(course ->
          course.getCourseDetail().getProperties().get("address") == null &&
          course.getCourseType().getType().equals("MANAGER"));
    }

    return toOutput(enrollments, courses, learnerId, input.getPublishState());
  }

  private List<CourseDto> toOutput(List<CourseEnrollment> enrollments, List<Course> courses, LearnerId learnerId, String publishState)
  {
    Map<CourseId, CourseEnrollment> courseEnrollmentMap = enrollments.stream()
        .collect(Collectors.toMap(CourseEnrollment::getCourseId, enrollment -> enrollment));
    Set<CourseId> enrolledCourseIds = enrollments.stream().map(CourseEnrollment::getCourseId).collect(Collectors.toSet());

    List<CourseDto> result = new ArrayList<>();

     for (Course course : courses)
    {
      if (enrolledCourseIds.contains(course.getCourseId()))
      {
        if (!StringUtils.isBlank(publishState))
        {
          String state = course.getCourseDetail().getProperties().get("state");
          if (!StringUtils.isBlank(state) && state.equals(publishState))
          {
            CourseDto courseDto = toCourseDto(course);
            courseDto.setProgress(lmsServiceRegistry.getProgressTrackingService().getLearnerProgress(learnerId.getId(), course.getCourseId().getId()));
            courseDto.setEnrollmentState(courseEnrollmentMap.get(course.getCourseId()).getEnrollmentState().name());
            result.add(courseDto);
          }
        }
        else
        {
          CourseDto courseDto = toCourseDto(course);
          courseDto.setProgress(lmsServiceRegistry.getProgressTrackingService().getLearnerProgress(learnerId.getId(), course.getCourseId().getId()));
          courseDto.setEnrollmentState(courseEnrollmentMap.get(course.getCourseId()).getEnrollmentState().name());
          result.add(courseDto);
        }
      }
    }
    Set<String> duplicateSet = new HashSet<>();
    List<CourseDto> removedDuplicates = result.stream().filter(e -> duplicateSet.add(e.getId())).collect(Collectors.toList());
    removedDuplicates = new ArrayList<>(removedDuplicates);
    return removedDuplicates;
  }

  private List<Course> getCourses(GetCoursesInput input) throws UseCaseException
  {
    CourseCategoryId courseCategoryId = CourseCategoryId.valueOf(input.getCategoryId());
    Optional<String> publishStatus = Optional.ofNullable(input.getPublishStatus());
    if (input.getCourseType() != null)
    {
      CourseType courseType = getCourseType(input.getCourseType());

      return publishStatus.map(status -> getCourses(courseCategoryId, courseType, PublishStatus.valueOf(status)))
          .orElseGet(() -> getCourses(courseCategoryId, courseType));
    }
    else
    {
      return publishStatus.map(status -> getCourses(courseCategoryId, PublishStatus.valueOf(status)))
          .orElseGet(() -> getCourses(courseCategoryId));
    }
  }

  private List<Course> getCourses(CourseCategoryId courseCategoryId, PublishStatus publishStatus)
  {
    return courseRepository.listAll(courseCategoryId, publishStatus);
  }

  private List<Course> getCourses(CourseCategoryId courseCategoryId)
  {
    return courseRepository.listAll(courseCategoryId);
  }

  private List<Course> getCourses(CourseCategoryId courseCategoryId, CourseType courseType)
  {
    return courseRepository.listAll(courseCategoryId, courseType);
  }

  private List<Course> getCourses(CourseCategoryId courseCategoryId, CourseType courseType, PublishStatus publishStatus)
  {
    return courseRepository.listAll(courseCategoryId, courseType, publishStatus);
  }
}

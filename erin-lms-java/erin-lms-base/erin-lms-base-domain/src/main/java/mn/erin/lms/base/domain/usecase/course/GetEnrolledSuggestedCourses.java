package mn.erin.lms.base.domain.usecase.course;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.content.CourseContent;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.aim.user.Learner;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.repository.CourseContentRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.base.domain.usecase.course.dto.GetCoursesInput;

/**
 * @author Erdenetulga
 */
@Authorized(users = { Supervisor.class, Learner.class })
public class GetEnrolledSuggestedCourses extends CourseUseCase<GetCoursesInput, List<CourseDto>>
{
  private final CourseContentRepository courseContentRepository;

  public GetEnrolledSuggestedCourses(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.courseContentRepository = lmsRepositoryRegistry.getCourseContentRepository();
  }

  @Override
  public List<CourseDto> execute(GetCoursesInput input) throws UseCaseException
  {
    LearnerId learnerId = LearnerId.valueOf(input.getLearnerId());
    List<CourseEnrollment> enrollments = courseEnrollmentRepository.listAll(learnerId);
    enrollments.sort(Collections.reverseOrder());

    List<Course> courses = new ArrayList<>();

    for (CourseEnrollment enrollment: enrollments)
    {
      if (courses.size() >= input.getCourseCount())
      {
        break;
      }

      try
      {
        Course course = getCourse(enrollment.getCourseId());
        if (course.getCourseCategoryId().getId().equals(input.getCategoryId()))
        {
          courses.add(course);
        }
      }
      catch (UseCaseException ignored)
      {
        // Ignored
      }
    }

    return toOutput(enrollments, courses, learnerId);
  }

  private List<CourseDto> toOutput(List<CourseEnrollment> enrollments, List<Course> courses, LearnerId learnerId)
  {
    Map<CourseId, CourseEnrollment> courseEnrollmentMap = enrollments.stream()
        .collect(Collectors.toMap(CourseEnrollment::getCourseId, enrollment -> enrollment));
    Set<CourseId> enrolledCourseIds = enrollments.stream().map(CourseEnrollment::getCourseId).collect(Collectors.toSet());

    List<CourseDto> result = new ArrayList<>();
    int modulesCount = 0;
    for (Course course : courses)
    {
      if (enrolledCourseIds.contains(course.getCourseId()))
      {
        try
        {
          CourseContent courseContent = courseContentRepository.fetchById(course.getCourseId());
          modulesCount = courseContent.getModules().size();
        }
        catch (LmsRepositoryException e)
        {
          // Ignored
        }
        CourseDto courseDto = toAlternateCourseDto(course, modulesCount, null);
        courseDto.setProgress(lmsServiceRegistry.getProgressTrackingService().getLearnerProgress(learnerId.getId(), course.getCourseId().getId()));
        courseDto.setEnrollmentState(courseEnrollmentMap.get(course.getCourseId()).getEnrollmentState().name());
        result.add(courseDto);
      }
    }

    return result;
  }

}

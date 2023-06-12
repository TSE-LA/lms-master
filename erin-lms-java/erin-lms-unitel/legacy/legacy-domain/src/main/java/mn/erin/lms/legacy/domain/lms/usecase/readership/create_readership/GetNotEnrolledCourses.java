package mn.erin.lms.legacy.domain.lms.usecase.readership.create_readership;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.role.LmsRole;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.PublishStatus;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollment;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;

/**
 * @author Munkh
 */
public class GetNotEnrolledCourses implements UseCase<GetNotEnrolledCoursesInput,  List<Course>>
{
  private final AccessIdentityManagement accessIdentityManagement;
  private final CourseRepository courseRepository;
  private final CourseEnrollmentRepository courseEnrollmentRepository;

  public GetNotEnrolledCourses(AccessIdentityManagement accessIdentityManagement,
      CourseRepository courseRepository, CourseEnrollmentRepository courseEnrollmentRepository)
  {
    this.accessIdentityManagement = accessIdentityManagement;
    this.courseRepository = courseRepository;
    this.courseEnrollmentRepository = courseEnrollmentRepository;
  }

  @Override
  public List<Course> execute(GetNotEnrolledCoursesInput input) throws UseCaseException
  {
    String role = accessIdentityManagement.getRole(input.getLearnerId());

    Set<String> groups;
    if (role.equalsIgnoreCase(LmsRole.LMS_ADMIN.name()))
    {
      groups = accessIdentityManagement.getSubDepartments(input.getGroupId());
    }
    else
    {
      groups = new HashSet<>();
      groups.add(input.getGroupId());
      if (!StringUtils.isBlank(input.getNewGroupId()))
      {
        groups.add(input.getNewGroupId());
      }
    }

    List<Course> courses = courseRepository.getCourseList(groups);

    List<CourseEnrollment> courseEnrollments = courseEnrollmentRepository.getEnrollmentList(new LearnerId(input.getLearnerId()));

    return courses.stream().filter(course ->
        courseEnrollments.stream().noneMatch(courseEnrollment ->
            courseEnrollment.getCourseId().equals(course.getCourseId())) &&
            PublishStatus.PUBLISHED == course.getCourseDetail().getPublishStatus()
    ).collect(Collectors.toList());
  }
}

package mn.erin.lms.unitel.domain.usecase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.category.CourseCategory;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.PublishStatus;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.unitel.domain.model.online_course.OnlineCourseType;
import mn.erin.lms.unitel.domain.usecase.dto.CourseCountByCategory;
import mn.erin.lms.unitel.domain.usecase.dto.CourseCountDto;
import mn.erin.lms.unitel.domain.usecase.dto.GetCourseCountInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Supervisor.class })
public class GetEnrolledCourseCount extends CourseUseCase<GetCourseCountInput, List<CourseCountDto>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetEnrolledCourseCount.class);

  public GetEnrolledCourseCount(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  public List<CourseCountDto> execute(GetCourseCountInput input) throws UseCaseException
  {
    List<Course> courses = getCourses(input);
    List<CourseCountDto> courseCount = new ArrayList<>();
    courseCount.add(getCourseCount(courses, OnlineCourseType.EMPLOYEE.name(), input.getParentCategoryId()));
    courseCount.add(getCourseCount(courses, OnlineCourseType.SUPERVISOR.name(), input.getParentCategoryId()));
    courseCount.add(getCourseCount(courses, OnlineCourseType.MANAGER.name(), input.getParentCategoryId()));
    return courseCount;
  }

  private ArrayList<Course> getCourses(GetCourseCountInput input)
  {
    String currentUser = lmsServiceRegistry.getAuthenticationService().getCurrentUsername();
    List<CourseEnrollment> enrollments = courseEnrollmentRepository.listAll(LearnerId.valueOf(currentUser));
    ArrayList<Course> courses = new ArrayList<>();
    for (CourseEnrollment enrollment : enrollments)
    {
      try
      {
        Course course = courseRepository.fetchById(enrollment.getCourseId(), input.getStartDate(), input.getEndDate());
        courses.add(course);
      }
      catch (LmsRepositoryException e)
      {
        LOGGER.error(e.getMessage());
      }
    }

    return courses;
  }

  private CourseCountDto getCourseCount(List<Course> courses, String courseType, String parentCategoryId)
  {
    String organizationId = lmsServiceRegistry.getOrganizationIdProvider().getOrganizationId();
    Collection<CourseCategory> categories = courseCategoryRepository.listAll(OrganizationId.valueOf(organizationId),
        CourseCategoryId.valueOf(parentCategoryId));
    Set<CourseCountByCategory> courseCountByCategories = new HashSet<>();
    for (CourseCategory category : categories)
    {
      Integer published = getCount(courses, category.getCourseCategoryId().getId(), PublishStatus.PUBLISHED, courseType);
      Integer unpublished = getCount(courses, category.getCourseCategoryId().getId(), PublishStatus.UNPUBLISHED, courseType);
      courseCountByCategories.add(new CourseCountByCategory(category.getName(), unpublished, published, category.getCourseCategoryId().getId()));
    }
    return new CourseCountDto(courseType, courseCountByCategories);
  }

  private int getCount(List<Course> courses, String categoryId, PublishStatus publishStatus, String courseType)
  {
    return (int) courses.stream()
        .filter(course -> categoryId.equals(course.getCourseCategoryId().getId()) && course.getCourseDetail().getPublishStatus() == publishStatus &&
            courseType.equals(course.getCourseType().getType()))
        .count();
  }
}

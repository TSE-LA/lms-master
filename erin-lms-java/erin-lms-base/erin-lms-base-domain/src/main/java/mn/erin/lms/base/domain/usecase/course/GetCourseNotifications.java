package mn.erin.lms.base.domain.usecase.course;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.model.EntityId;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.domain.model.enrollment.EnrollmentState;
import mn.erin.lms.base.aim.user.Learner;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.dto.CourseNotificationDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Supervisor.class, Learner.class })
public class GetCourseNotifications extends CourseUseCase<Void, List<CourseNotificationDto>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetCourseNotifications.class);


  public GetCourseNotifications(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  public List<CourseNotificationDto> execute(Void input)
  {
    LmsUser currentUser = lmsServiceRegistry.getLmsUserService().getCurrentUser();
    EntityId currentUserId = currentUser.getId();
    return getCourseNotifications(currentUserId);
  }

  private List<CourseNotificationDto> getCourseNotifications(EntityId currentUserId)
  {
    List<CourseEnrollment> enrollments = courseEnrollmentRepository.listAll(LearnerId.valueOf(currentUserId.getId()));
    Map<String, Integer> newCoursesCountByCategory = getNewCoursesCountByCategory(enrollments);
    return getCourseNotifications(newCoursesCountByCategory);
  }

  private Map<String, Integer> getNewCoursesCountByCategory(List<CourseEnrollment> enrollments)
  {
    Map<String, Integer> newCoursesCountByCategory = new HashMap<>();

    for (CourseEnrollment enrollment : enrollments)
    {
      if (enrollment.getEnrollmentState() == EnrollmentState.NEW)
      {
        try
        {
          Course course = courseRepository.fetchById(enrollment.getCourseId());
          String categoryId = course.getCourseCategoryId().getId();
          Integer count = newCoursesCountByCategory.get(categoryId);

          if (count != null)
          {
            count += 1;
            newCoursesCountByCategory.put(categoryId, count);
          }
          else
          {
            newCoursesCountByCategory.put(categoryId, 1);
          }
        }
        catch (LmsRepositoryException e)
        {
          LOGGER.error(e.getMessage(), e);
        }
      }
    }

    return newCoursesCountByCategory;
  }

  private List<CourseNotificationDto> getCourseNotifications(Map<String, Integer> newCoursesCountByCategory)
  {
    List<CourseNotificationDto> courseNotifications = new ArrayList<>();

    for (Map.Entry<String, Integer> entry : newCoursesCountByCategory.entrySet())
    {
      courseNotifications.add(new CourseNotificationDto(entry.getKey(), entry.getValue()));
    }

    return courseNotifications;
  }
}

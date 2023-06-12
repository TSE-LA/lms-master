package mn.erin.lms.base.domain.usecase.notification;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.NotificationService;
import mn.erin.lms.base.domain.usecase.LmsUseCase;
import mn.erin.lms.base.domain.model.classroom_course.Attendance;
import mn.erin.lms.base.domain.model.classroom_course.ClassroomCourseAttendance;
import mn.erin.lms.base.domain.repository.ClassroomCourseAttendanceRepository;

/**
 * @author Byambajav.
 */

@Authorized(users = { Author.class, Instructor.class })
public class SendAssessmentLinkNotification extends LmsUseCase<String, Boolean>
{
  private final ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository;
  private final CourseRepository courseRepository;
  private final NotificationService notificationService;
  private final Map<String, Object> data = new HashMap<>();

  public SendAssessmentLinkNotification(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry, ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.courseRepository = lmsRepositoryRegistry.getCourseRepository();
    this.notificationService = lmsServiceRegistry.getNotificationService();
    this.classroomCourseAttendanceRepository = classroomCourseAttendanceRepository;
  }

  @Override
  protected Boolean executeImpl(String courseId) throws UseCaseException
  {
    ClassroomCourseAttendance classroomCourseAttendance;
    Set<String> recipients = new HashSet<>();
    {
      if (StringUtils.isBlank(courseId))
      {
        throw new UseCaseException("Input cannot be null or blank!");
      }
      try
      {
        classroomCourseAttendance = classroomCourseAttendanceRepository.findByCourseId(CourseId.valueOf(courseId));
      }
      catch (LmsRepositoryException e)
      {
        throw new UseCaseException(e.getMessage(), e);
      }
    }
    fillData(courseId);
    for (Attendance attendance : classroomCourseAttendance.getAttendances())
    {
      if (attendance.isPresent())
      {
        recipients.add(attendance.getLearnerId().getId());
      }
    }
    notificationService.notifyClassroomCourseClosed(recipients, this.data);

    return true;
  }

  private void fillData(String courseId) throws UseCaseException
  {
    String courseTitle;

    try
    {
      Course course = courseRepository.fetchById(CourseId.valueOf(courseId));
      courseTitle = course.getCourseDetail().getTitle();
      data.put("courseTitle", courseTitle);
      data.put("courseId", courseId);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

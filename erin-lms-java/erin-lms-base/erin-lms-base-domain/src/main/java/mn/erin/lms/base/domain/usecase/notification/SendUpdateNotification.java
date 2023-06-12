package mn.erin.lms.base.domain.usecase.notification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.domain.repository.CourseEnrollmentRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.NotificationService;
import mn.erin.lms.base.domain.usecase.LmsUseCase;
import mn.erin.lms.base.domain.usecase.notification.dto.SendNotificationInput;

/**
 * @author Munkh
 */
@Authorized(users = { Author.class, Instructor.class })
public class SendUpdateNotification extends LmsUseCase<SendNotificationInput, Boolean>
{
  private final CourseEnrollmentRepository courseEnrollmentRepository;
  private final CourseRepository courseRepository;
  private final NotificationService notificationService;
  private Map<String, Object> data = new HashMap<>();

  public SendUpdateNotification(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.courseEnrollmentRepository = lmsRepositoryRegistry.getCourseEnrollmentRepository();
    this.courseRepository = lmsRepositoryRegistry.getCourseRepository();
    this.notificationService = lmsServiceRegistry.getNotificationService();
  }

  @Override
  protected Boolean executeImpl(SendNotificationInput input) throws UseCaseException
  {
    List<CourseEnrollment> enrollmentList = courseEnrollmentRepository.listAll(CourseId.valueOf(input.getCourseId()));
    fillData(input.getCourseId());

    Set<String> recipients = enrollmentList.stream().map(enrollment -> enrollment.getLearnerId().getId()).collect(Collectors.toSet());

    notificationService.notifyClassroomCourseUpdated(recipients, this.data, "Танхимын сургалт өөрчлөгдлөө.", "classroom-update.ftl");

    return true;
  }

  private void fillData(String courseId) throws UseCaseException
  {
    String courseTitle;
    String date;
    String startTime;
    String endTime;
    String location;

    try
    {
      Course course = courseRepository.fetchById(CourseId.valueOf(courseId));
      courseTitle = course.getCourseDetail().getTitle();
      date = course.getCourseDetail().getProperties().get("date");
      startTime = course.getCourseDetail().getProperties().get("startTime");
      endTime = course.getCourseDetail().getProperties().get("endTime");
      location = course.getCourseDetail().getProperties().get("address");
      data.put("courseTitle", courseTitle);
      data.put("date", date);
      data.put("startTime", startTime);
      data.put("endTime", endTime);
      data.put("location", location);
      data.put("authorId", lmsServiceRegistry.getAccessIdentityManagement().getCurrentUsername());
      data.put("courseId", course.getCourseId().getId());
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

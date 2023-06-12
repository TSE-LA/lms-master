package mn.erin.lms.base.domain.usecase.course;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.CourseType;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.domain.repository.ClassroomCourseAttendanceRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.UnknownCourseTypeException;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.base.domain.usecase.course.dto.UpdateStateInput;

/**
 * @author Munkh
 */
public class UpdateCourseState extends CourseUseCase<UpdateStateInput, CourseDto>
{
  private static final String PREVIOUS_STATE = "previousState";
  private static final String STATE = "state";
  private static final String STATE_NEW = "NEW";
  private static final String STATE_READY = "READY";
  public static final String CANCELED = "CANCELED";
  public static final String POSTPONED = "POSTPONED";
  public static final String REASON = "reason";
  private final ClassroomCourseAttendanceRepository attendanceRepository;
  public UpdateCourseState(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry, ClassroomCourseAttendanceRepository attendanceRepository)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.attendanceRepository = attendanceRepository;
  }

  @Override
  public CourseDto execute(UpdateStateInput input) throws UseCaseException
  {
    Course course = getCourse(CourseId.valueOf(input.getCourseId()));
    CourseType courseType;
    try
    {
      courseType = courseTypeResolver.resolve(course.getCourseType().getType());
    }
    catch (UnknownCourseTypeException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }

    String assessmentId = course.getAssessmentId();
    String certificateId = course.getCertificateId();
    CourseDetail courseDetail = course.getCourseDetail();

    if (input.isRollback())
    {
      String previousState = courseDetail.getProperties().get(PREVIOUS_STATE);
      if (previousState == null) // In case of previous state isn't saved
      {
        try
        {
          attendanceRepository.findByCourseId(course.getCourseId());
          courseDetail.getProperties().put(STATE, STATE_READY);
        }
        catch (LmsRepositoryException ignored)
        {
          courseDetail.getProperties().put(STATE, STATE_NEW);
        }
      }
      else
      {
        courseDetail.getProperties().put(STATE, previousState);
      }
      courseDetail.getProperties().put(PREVIOUS_STATE, null);
    }
    else
    {
      courseDetail.getProperties().put(PREVIOUS_STATE, courseDetail.getProperties().get(STATE));
      courseDetail.getProperties().put(STATE, input.getState());

      if (input.getState().equals(CANCELED))
      {
        sendNotification(course, input.getReason(), "Танхимын сургалт цуцлагдлаа.", "classroom-cancel.ftl", CANCELED);
      }
      else if (input.getState().equals(POSTPONED))
      {
        sendNotification(course, input.getReason(), "Танхимын сургалт хойшлогдлоо.", "classroom-postpone.ftl", POSTPONED);
      }

    }

    if (input.getReason() != null && !input.getReason().equals(""))
    {
      courseDetail.getProperties().put(REASON, input.getReason());
    }

    try
    {
      Course updatedCourse = courseRepository.update(CourseId.valueOf(input.getCourseId()), CourseCategoryId.valueOf(course.getCourseCategoryId().getId()),
          courseDetail, courseType, assessmentId, certificateId);
      return toCourseDto(updatedCourse);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private void sendNotification(Course course, String reason, String subject, String template, String state){
    List<CourseEnrollment> enrollments = courseEnrollmentRepository.listAll(course.getCourseId());

    Map<String, Object> data = new HashMap<>();
    String date;
    String startTime;
    String endTime;
    String authorId;
    String courseTitle;

    LmsUser lmsUser = lmsServiceRegistry.getLmsUserService().getCurrentUser();
    authorId = lmsUser.getId().getId();
    courseTitle = course.getCourseDetail().getTitle();
    date = course.getCourseDetail().getProperties().get("date");
    startTime = course.getCourseDetail().getProperties().get("startTime");
    endTime = course.getCourseDetail().getProperties().get("endTime");

    data.put("authorId", authorId);
    data.put("date", date);
    data.put("startTime", startTime);
    data.put("endTime", endTime);
    data.put("courseTitle", courseTitle);
    data.put(REASON, reason);
    data.put("courseId", course.getCourseId().getId());

    Set<String> recipients = new HashSet<>();
    for (CourseEnrollment enrollment : enrollments)
    {
      String learnerId = enrollment.getLearnerId().getId();
      recipients.add(learnerId);
    }

    lmsServiceRegistry.getNotificationService().notifyCourseStateUpdated(recipients, data, subject, template, state);
  }

}

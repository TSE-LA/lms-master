package mn.erin.lms.base.domain.usecase.course.dto;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class PublishCourseInput
{
  private final String courseId;
  private final Set<String> assignedDepartments;
  private final Set<String> assignedLearners;
  //    TODO: Remove this field when version is 3.0.0
  private boolean autoChildDepartmentEnroll = true;

  private NotificationInput notificationInput;

  private Date publishDate;

  public PublishCourseInput(String courseId, Set<String> assignedDepartments, Set<String> assignedUsers)
  {
    this.courseId = courseId;
    this.assignedDepartments = assignedDepartments;
    this.assignedLearners = assignedUsers;
  }

  public void setNotificationInput(NotificationInput notificationInput)
  {
    this.notificationInput = notificationInput;
  }

  public void setPublishDate(Date publishDate)
  {
    this.publishDate = publishDate;
  }

  public NotificationInput getNotificationInput()
  {
    return notificationInput;
  }

  public Date getPublishDate()
  {
    return publishDate;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public Set<String> getAssignedDepartments()
  {
    return Collections.unmodifiableSet(assignedDepartments);
  }

  public Set<String> getAssignedLearners()
  {
    return Collections.unmodifiableSet(assignedLearners);
  }

  public boolean hasAutoChildDepartmentEnroll()
  {
    return autoChildDepartmentEnroll;
  }

  public void setAutoChildDepartmentEnroll(boolean autoChildDepartmentEnroll)
  {
    this.autoChildDepartmentEnroll = autoChildDepartmentEnroll;
  }
}

package mn.erin.lms.base.domain.usecase.enrollment.dto;

import java.util.Collections;
import java.util.Set;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class UpdateCourseEnrollmentsInput
{
  private final String courseId;
  private final Set<String> assignedDepartments;
  private final Set<String> assignedLearners;
  private boolean sendNotification = false;
  private String courseType;
  //    TODO: Remove this field when version is 3.0.0
  private boolean autoChildDepartmentEnroll = true;

  public UpdateCourseEnrollmentsInput(String courseId, Set<String> assignedDepartments, Set<String> assignedLearners)
  {
    this.courseId = courseId;
    this.assignedDepartments = assignedDepartments;
    this.assignedLearners = assignedLearners;
  }
  public UpdateCourseEnrollmentsInput(String courseId, Set<String> assignedDepartments, Set<String> assignedLearners, String courseType)
  {
    this.courseId = courseId;
    this.assignedDepartments = assignedDepartments;
    this.assignedLearners = assignedLearners;
    this.courseType = courseType;
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

  public boolean isSendNotification()
  {
    return sendNotification;
  }

  public void setSendNotification(boolean sendNotification)
  {
    this.sendNotification = sendNotification;
  }

  public String getCourseType()
  {
    return courseType;
  }

  public void setCourseType(String courseType)
  {
    this.courseType = courseType;
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

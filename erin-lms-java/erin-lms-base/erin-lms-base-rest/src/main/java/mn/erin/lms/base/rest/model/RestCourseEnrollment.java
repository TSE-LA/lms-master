package mn.erin.lms.base.rest.model;

import java.util.Set;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestCourseEnrollment
{
  private Set<String> departments;
  private Set<String> learners;
  private boolean sendNotification;
  private String courseType;
  //    TODO: Remove this field when version is 3.0.0
  private boolean autoChildDepartmentEnroll = true;

  public Set<String> getDepartments()
  {
    return departments;
  }

  public void setDepartments(Set<String> departments)
  {
    this.departments = departments;
  }

  public Set<String> getLearners()
  {
    return learners;
  }

  public void setLearners(Set<String> learners)
  {
    this.learners = learners;
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

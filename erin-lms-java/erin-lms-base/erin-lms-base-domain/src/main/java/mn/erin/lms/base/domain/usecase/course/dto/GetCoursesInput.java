package mn.erin.lms.base.domain.usecase.course.dto;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetCoursesInput
{
  private final String categoryId;

  private String courseType;
  private String publishStatus;
  private String publishState;
  private String learnerId;
  private String role;
  private int courseCount;

  public GetCoursesInput(String categoryId)
  {
    this.categoryId = Validate.notBlank(categoryId);
  }

  public void setCourseType(String courseType)
  {
    this.courseType = courseType;
  }

  public void setPublishStatus(String publishStatus)
  {
    this.publishStatus = publishStatus;
  }

  public void setPublishState(String publishState)
  {
    this.publishState = publishState;
  }

  public void setLearnerId(String learnerId)
  {
    this.learnerId = learnerId;
  }

  public String getCategoryId()
  {
    return categoryId;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public String getPublishStatus()
  {
    return publishStatus;
  }

  public String getPublishState()
  {
    return publishState;
  }

  public String getCourseType()
  {
    return courseType;
  }

  public int getCourseCount()
  {
    return courseCount;
  }

  public void setCourseCount(int courseCount)
  {
    this.courseCount = courseCount;
  }

  public String getRole()
  {
    return role;
  }

  public void setRole(String role)
  {
    this.role = role;
  }
}

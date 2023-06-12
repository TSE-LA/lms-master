package mn.erin.lms.base.domain.usecase.course.dto;

import java.util.Map;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreateCourseInput
{
  private final String title;
  private final String categoryId;
  private final Map<String, String> properties;

  private String description;
  private String courseType;
  private String assessmentId;
  private String certificateId;
  private double credit;

  public CreateCourseInput(String title, String categoryId, Map<String, String> properties)
  {
    this.title = Validate.notBlank(title);
    this.categoryId = Validate.notBlank(categoryId);
    this.properties = Validate.notNull(properties);
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public void setCourseType(String courseType)
  {
    this.courseType = courseType;
  }

  public String getTitle()
  {
    return title;
  }

  public String getCategoryId()
  {
    return categoryId;
  }

  public Map<String, String> getProperties()
  {
    return properties;
  }

  public String getDescription()
  {
    return description;
  }

  public String getCourseType()
  {
    return courseType;
  }

  public String getAssessmentId() {
    return assessmentId;
  }

  public void setAssessmentId(String assessmentId) {
    this.assessmentId = assessmentId;
  }

  public String getCertificateId() {
    return certificateId;
  }

  public void setCertificateId(String certificateId) {
    this.certificateId = certificateId;
  }

  public double getCredit()
  {
    return credit;
  }

  public void setCredit(double credit)
  {
    this.credit = credit;
  }
}

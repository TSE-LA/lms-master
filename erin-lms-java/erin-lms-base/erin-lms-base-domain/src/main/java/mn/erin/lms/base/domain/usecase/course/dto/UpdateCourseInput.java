package mn.erin.lms.base.domain.usecase.course.dto;

import java.util.Map;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class UpdateCourseInput
{
  private final String courseId;
  private final String title;
  private final String categoryId;
  private final Map<String, String> properties;

  private String description;
  private String assessmentId;
  private String certificateId;
  private String type;
  private boolean withThumbnail;
  private boolean hasQuiz;
  private boolean hasFeedback;
  private boolean hasAssessment;
  private boolean hasCertificate;

  private String emailSubject;
  private String templateName;
  private String note;
  private double credit;

  public UpdateCourseInput(String courseId, String title, String categoryId, Map<String, String> properties)
  {
    this.courseId = Validate.notBlank(courseId);
    this.title = Validate.notBlank(title);
    this.categoryId = Validate.notBlank(categoryId);
    this.properties = Validate.notNull(properties);
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public String getTitle()
  {
    return title;
  }

  public String getCategoryId()
  {
    return categoryId;
  }

  public String getType()
  {
    return type;
  }

  public Map<String, String> getProperties()
  {
    return properties;
  }

  public String getDescription()
  {
    return description;
  }

  public boolean getHasQuiz() {
    return hasQuiz;
  }

  public void setHasQuiz(boolean hasQuiz) {
    this.hasQuiz = hasQuiz;
  }

  public boolean getHasFeedback() {
    return hasFeedback;
  }

  public void setHasFeedback(boolean hasFeedback) {
    this.hasFeedback = hasFeedback;
  }

  public boolean isHasAssessment() {
    return hasAssessment;
  }

  public void setHasAssessment(boolean hasAssessment) {
    this.hasAssessment = hasAssessment;
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

  public boolean isHasCertificate() {
    return hasCertificate;
  }

  public void setHasCertificate(boolean hasCertificate) {
    this.hasCertificate = hasCertificate;
  }

  public String getEmailSubject()
  {
    return emailSubject;
  }

  public void setEmailSubject(String emailSubject)
  {
    this.emailSubject = emailSubject;
  }

  public String getTemplateName()
  {
    return templateName;
  }

  public void setTemplateName(String templateName)
  {
    this.templateName = templateName;
  }

  public String getNote()
  {
    return note;
  }

  public void setNote(String note)
  {
    this.note = note;
  }

  public boolean isWithThumbnail()
  {
    return withThumbnail;
  }

  public void setWithThumbnail(boolean withThumbnail)
  {
    this.withThumbnail = withThumbnail;
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

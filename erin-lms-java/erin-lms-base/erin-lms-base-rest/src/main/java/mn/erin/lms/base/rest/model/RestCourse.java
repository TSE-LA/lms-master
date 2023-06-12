package mn.erin.lms.base.rest.model;

import java.util.Map;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestCourse
{
  private String title;
  private String categoryId;
  private String description;
  private String type;
  private String note;
  private String assessmentId;
  private String certificateId;
  private boolean withThumbnail;
  private boolean hasQuiz;
  private boolean hasFeedBack;
  private boolean hasAssessment;
  private boolean hasCertificate;
  private String emailSubject;
  private String templateName;
  private double credit;

  private Map<String, String> properties;

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getCategoryId()
  {
    return categoryId;
  }

  public void setCategoryId(String categoryId)
  {
    this.categoryId = categoryId;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public String getNote()
  {
    return note;
  }

  public void setNote(String note)
  {
    this.note = note;
  }

  public Map<String, String> getProperties()
  {
    return properties;
  }

  public void setProperties(Map<String, String> properties)
  {
    this.properties = properties;
  }

  public boolean isHasQuiz() {
    return hasQuiz;
  }

  public void setHasQuiz(boolean hasQuiz) {
    this.hasQuiz = hasQuiz;
  }

  public boolean isHasFeedBack() {
    return hasFeedBack;
  }

  public void setHasFeedBack(boolean hasFeedBack) {
    this.hasFeedBack = hasFeedBack;
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

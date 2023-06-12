package mn.erin.lms.base.mongo.document.course;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Document
public class MongoCourse
{
  @Id
  private String id;

  @Indexed
  private String categoryId;

  @Indexed
  private String categoryName;

  @Indexed
  private String authorId;

  @Indexed
  private String courseType;

  @Indexed
  private String title;

  @Indexed
  private MongoPublishStatus publishStatus;

  @Indexed
  private LocalDateTime createdDate;

  @Indexed
  private LocalDateTime modifiedDate;

  @Indexed
  private String description;

  @Indexed
  private String courseDepartment;

  @Indexed
  private String progress;

  @Indexed
  private Set<String> assignedDepartments = new HashSet<>();

  @Indexed
  private Set<String> assignedLearners = new HashSet<>();

  private String courseContentId;
  private String thumbnailUrl;
  private String assessmentId;
  private String certificateId;
  private LocalDateTime publishDate;

  private boolean hasQuiz = false;
  private boolean hasFeedbackOption = false;
  private boolean hasAssessment = false;
  private boolean hasCertificate = false;
  private double credit;
  private int certifiedNumber;

  private Map<String, String> properties = new HashMap<>();

  public MongoCourse()
  {
  }

  public MongoCourse(String id, String title, String categoryId, MongoPublishStatus publishStatus, String authorId)
  {
    this.id = Validate.notBlank(id);
    this.title = Validate.notBlank(title);
    this.publishStatus = Validate.notNull(publishStatus);
    this.categoryId = Validate.notBlank(categoryId);
    this.authorId = Validate.notBlank(authorId);
  }

  public MongoCourse(String id, String title, String categoryId, String courseType, MongoPublishStatus publishStatus, String authorId)
  {
    this.id = Validate.notBlank(id);
    this.title = Validate.notBlank(title);
    this.publishStatus = Validate.notNull(publishStatus);
    this.categoryId = Validate.notBlank(categoryId);
    this.courseType = Validate.notBlank(courseType);
    this.authorId = Validate.notBlank(authorId);
  }

  @Override
  public String toString()
  {
    return String.format("Course[id=%s, title=%s]", id, title);
  }

  public void setCategoryId(String categoryId)
  {
    this.categoryId = categoryId;
  }

  public void setCourseType(String courseType)
  {
    this.courseType = courseType;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public void setCourseContentId(String courseContentId)
  {
    this.courseContentId = courseContentId;
  }

  public void setPublishStatus(MongoPublishStatus publishStatus)
  {
    this.publishStatus = publishStatus;
  }

  public void setCreatedDate(LocalDateTime createdDate)
  {
    this.createdDate = createdDate;
  }

  public void setModifiedDate(LocalDateTime modifiedDate)
  {
    this.modifiedDate = modifiedDate;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public void setProperties(Map<String, String> properties)
  {
    this.properties = properties;
  }

  public void setCourseDepartment(String courseDepartment)
  {
    this.courseDepartment = courseDepartment;
  }

  public void setAssignedDepartments(Set<String> assignedDepartments)
  {
    this.assignedDepartments = assignedDepartments;
  }

  public void setAssignedLearners(Set<String> assignedLearners)
  {
    this.assignedLearners = assignedLearners;
  }

  public void setPublishDate(LocalDateTime publishDate)
  {
    this.publishDate = publishDate;
  }

  public void setHasQuiz(boolean hasQuiz)
  {
    this.hasQuiz = hasQuiz;
  }

  public void setHasFeedbackOption(boolean hasFeedbackOption)
  {
    this.hasFeedbackOption = hasFeedbackOption;
  }
  public void setHasAssessment(boolean hasAssessment)
  {
    this.hasAssessment = hasAssessment;
  }

  public void setThumbnailUrl(String thumbnailUrl)
  {
    this.thumbnailUrl = thumbnailUrl;
  }

  public String getId()
  {
    return id;
  }

  public String getCategoryId()
  {
    return categoryId;
  }

  public String getCourseType()
  {
    return courseType;
  }

  public String getTitle()
  {
    return title;
  }

  public MongoPublishStatus getPublishStatus()
  {
    return publishStatus;
  }

  public LocalDateTime getCreatedDate()
  {
    return createdDate;
  }

  public LocalDateTime getModifiedDate()
  {
    return modifiedDate;
  }

  public String getDescription()
  {
    return description;
  }

  public String getCourseContentId()
  {
    return courseContentId;
  }

  public String getAuthorId()
  {
    return authorId;
  }

  public String getCourseDepartment()
  {
    return courseDepartment;
  }

  public Set<String> getAssignedDepartments()
  {
    return assignedDepartments;
  }

  public Set<String> getAssignedLearners()
  {
    return assignedLearners;
  }

  public String getThumbnailUrl()
  {
    return thumbnailUrl;
  }

  public LocalDateTime getPublishDate()
  {
    return publishDate;
  }

  public boolean hasQuiz()
  {
    return hasQuiz;
  }

  public boolean hasFeedbackOption()
  {
    return hasFeedbackOption;
  }

  public Map<String, String> getProperties()
  {
    return properties;
  }

  public boolean isHasAssessment() {
    return hasAssessment;
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

  public String getProgress() {
    return progress;
  }

  public void setProgress(String progress) {
    this.progress = progress;
  }

  public String getCategoryName()
  {
    return categoryName;
  }

  public void setCategoryName(String categoryName)
  {
    this.categoryName = categoryName;
  }

  public double getCredit()
  {
    return credit;
  }

  public void setCredit(double credit)
  {
    this.credit = credit;
  }

  public int getCertifiedNumber()
  {
    return certifiedNumber;
  }

  public void setCertifiedNumber(int certifiedNumber)
  {
    this.certifiedNumber = certifiedNumber;
  }
}

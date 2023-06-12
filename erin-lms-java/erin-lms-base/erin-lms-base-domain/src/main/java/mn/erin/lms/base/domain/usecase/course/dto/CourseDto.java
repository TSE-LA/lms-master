package mn.erin.lms.base.domain.usecase.course.dto;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseDto
{
  private String id;
  private String courseCategoryId;
  private String courseCategoryName;
  private String courseContentId;
  private String type;
  private String authorId;
  private String title;
  private String description;
  private String publishStatus;
  private String thumbnailUrl;
  private String assessmentId;
  private String certificateId;
  private Float progress;
  private Date createdDate;
  private Date modifiedDate;
  private Date publishDate;
  private String enrollmentState;
  private Map<String, String> properties;
  private String belongingDepartmentId;
  private Set<String> assignedDepartments;
  private Map<String, String> assignedDepartmentNames;
  private Set<String> assignedLearners;
  private Set<String> suggestedLearners;
  private boolean hasQuiz;
  private boolean hasFeedback;
  private boolean hasAssessment;
  private boolean hasCertificate;
  private Object interactionsCount;
  private int modulesCount;
  private int enrollmentCount;
  private String belongingDepartmentName;
  private String durationTime;
  private double credit;

  private CourseDto()
  {
  }

  public void setEnrollmentState(String enrollmentState)
  {
    this.enrollmentState = enrollmentState;
  }

  public String getId()
  {
    return id;
  }

  public String getCourseCategoryId()
  {
    return courseCategoryId;
  }

  public String getCourseContentId()
  {
    return courseContentId;
  }

  public String getAuthorId()
  {
    return authorId;
  }

  public String getBelongingDepartmentId()
  {
    return belongingDepartmentId;
  }

  public String getType()
  {
    return type;
  }

  public String getTitle()
  {
    return title;
  }

  public String getDescription()
  {
    return description;
  }

  public String getPublishStatus()
  {
    return publishStatus;
  }

  public String getThumbnailUrl()
  {
    return thumbnailUrl;
  }

  public Date getCreatedDate()
  {
    return createdDate;
  }

  public Date getModifiedDate()
  {
    return modifiedDate;
  }

  public String getEnrollmentState()
  {
    return enrollmentState;
  }

  public Map<String, String> getProperties()
  {
    return Collections.unmodifiableMap(properties);
  }

  public void setSuggestedLearners(Set<String> suggestedLearners) { this.assignedLearners = suggestedLearners;}

  public Set<String> getSuggestedLearners() { return  this.suggestedLearners;}

  public Set<String> getAssignedDepartments()
  {
    return Collections.unmodifiableSet(assignedDepartments);
  }

  public Set<String> getAssignedLearners()
  {
    return Collections.unmodifiableSet(assignedLearners);
  }

  public boolean isHasQuiz()
  {
    return hasQuiz;
  }

  public boolean isHasFeedback()
  {
    return hasFeedback;
  }

  public boolean isHasAssessment()
  {
    return hasAssessment;
  }

  public void setHasAssessment(boolean hasAssessment)
  {
    this.hasAssessment = hasAssessment;
  }

  public String getAssessmentId()
  {
    return assessmentId;
  }

  public void setAssessmentId(String assessmentId)
  {
    this.assessmentId = assessmentId;
  }

  public String getCertificateId()
  {
    return certificateId;
  }

  public void setCertificateId(String certificateId)
  {
    this.certificateId = certificateId;
  }

  public boolean isHasCertificate()
  {
    return hasCertificate;
  }

  public void setHasCertificate(boolean hasCertificate)
  {
    this.hasCertificate = hasCertificate;
  }

  public Float getProgress()
  {
    return progress;
  }

  public void setProgress(Float progress)
  {
    this.progress = progress;
  }

  public String getCourseCategoryName()
  {
    return courseCategoryName;
  }

  public void setCourseCategoryName(String courseCategoryName)
  {
    this.courseCategoryName = courseCategoryName;
  }

  public Date getPublishDate()
  {
    return publishDate;
  }

  public void setPublishDate(Date publishDate)
  {
    this.publishDate = publishDate;
  }

  public int getModulesCount()
  {
    return modulesCount;
  }

  public void setModulesCount(int modulesCount)
  {
    this.modulesCount = modulesCount;
  }

  public Object getInteractionsCount()
  {
    return interactionsCount;
  }

  public void setInteractionsCount(Object interactionsCount)
  {
    this.interactionsCount = interactionsCount;
  }

  public int getEnrollmentCount()
  {
    return enrollmentCount;
  }

  public void setEnrollmentCount(int enrollmentCount)
  {
    this.enrollmentCount = enrollmentCount;
  }

  public String getBelongingDepartmentName()
  {
    return belongingDepartmentName;
  }

  public void setBelongingDepartmentName(String belongingDepartmentName)
  {
    this.belongingDepartmentName = belongingDepartmentName;
  }

  public String getDurationTime()
  {
    return durationTime;
  }

  public void setDurationTime(String durationTime)
  {
    this.durationTime = durationTime;
  }

  public Map<String, String> getAssignedDepartmentNames()
  {
    return assignedDepartmentNames;
  }

  public void setAssignedDepartmentNames(Map<String, String> assignedDepartmentNames)
  {
    this.assignedDepartmentNames = assignedDepartmentNames;
  }

  public double getCredit()
  {
    return credit;
  }

  public void setCredit(double credit)
  {
    this.credit = credit;
  }

  public static class Builder
  {
    private String id;
    private String courseCategoryId;
    private String courseCategoryName;
    private String courseContentId;
    private String type;
    private String authorId;
    private String title;
    private String description;
    private String publishStatus;
    private String thumbnailUrl;
    private int modulesCount;
    private Object interactionsCount;
    private String assessmentId;
    private String certificateId;
    private Float progress;
    private Date createdDate;
    private Date modifiedDate;
    private Date publishDate;
    private String belongDepartmentId;
    private String belongDepartmentName;
    private Map<String, String> properties;
    private Set<String> assignedDepartments;
    private Map<String, String> assignedDepartmentsEntity;
    private Set<String> assignedLearners;
    private boolean hasQuiz;
    private boolean hasFeedback;
    private boolean hasAssessment;
    private boolean hasCertificate;
    private int enrollmentCount;
    private Set<String> suggestedLearners;
    private double credit;

    public Builder(String id)
    {
      this.id = id;
    }

    public Builder ofCategory(String courseCategoryId)
    {
      this.courseCategoryId = courseCategoryId;
      return this;
    }

    public Builder ofCategoryName(String courseCategoryName)
    {
      this.courseCategoryName = courseCategoryName;
      return this;
    }

    public Builder withContent(String courseContentId)
    {
      this.courseContentId = courseContentId;
      return this;
    }

    public Builder withAuthor(String authorId)
    {
      this.authorId = authorId;
      return this;
    }

    public Builder withTitle(String title)
    {
      this.title = title;
      return this;
    }

    public Builder ofType(String type)
    {
      this.type = type;
      return this;
    }

    public Builder withDescription(String description)
    {
      this.description = description;
      return this;
    }

    public Builder withHasQuiz(boolean hasQuiz)
    {
      this.hasQuiz = hasQuiz;
      return this;
    }

    public Builder withHasFeedback(boolean hasFeedback)
    {
      this.hasFeedback = hasFeedback;
      return this;
    }

    public Builder withHasAssessment(boolean hasAssessment)
    {
      this.hasAssessment = hasAssessment;
      return this;
    }

    public Builder withHasCertificate(boolean hasCertificate)
    {
      this.hasCertificate = hasCertificate;
      return this;
    }

    public Builder withAssessmentId(String assessmentId)
    {
      this.assessmentId = assessmentId;
      return this;
    }

    public Builder withCertificateId(String certificateId)
    {
      this.certificateId = certificateId;
      return this;
    }

    public Builder withProgress(Float progress)

    {
      this.progress = progress;
      return this;
    }

    public Builder withPublishStatus(String publishStatus)
    {
      this.publishStatus = publishStatus;
      return this;
    }

    public Builder withProperties(Map<String, String> properties)
    {
      this.properties = properties;
      return this;
    }

    public Builder withAssignedDepartments(Set<String> assignedDepartments)
    {
      this.assignedDepartments = assignedDepartments;
      return this;
    }

    public Builder withAssignedDepartmentsEntity(Map<String, String> assignedDepartmentsEntity)
    {
      this.assignedDepartmentsEntity = assignedDepartmentsEntity;
      return this;
    }

    public Builder withAssignedLearners(Set<String> assignedLearners)
    {
      this.assignedLearners = assignedLearners;
      return this;
    }

    public Builder withThumbnailUrl(String thumbnailUrl)
    {
      this.thumbnailUrl = thumbnailUrl;
      return this;
    }

    public Builder withModulesCount(int modulesCount)
    {
      this.modulesCount = modulesCount;
      return this;
    }

    public Builder withInteractionsCount(Object interactionsCount)
    {
      this.interactionsCount = interactionsCount;
      return this;
    }

    public Builder createdAt(Date createdDate)
    {
      this.createdDate = createdDate;
      return this;
    }

    public Builder modifiedAt(Date modifiedDate)
    {
      this.modifiedDate = modifiedDate;
      return this;
    }

    public Builder scheduledAt(Date publishDate)
    {
      this.publishDate = publishDate;
      return this;
    }

    public Builder belongsTo(String departmentId)
    {
      this.belongDepartmentId = departmentId;
      return this;
    }

    public Builder belongsToName(String departmentName)
    {
      this.belongDepartmentName = departmentName;
      return this;
    }

    public Builder withEnrollmentCount(int enrollmentCount)
    {
      this.enrollmentCount = enrollmentCount;
      return this;
    }

    public Builder withSuggestedLearners(Set<String> suggestedLearners)
    {
      this.suggestedLearners = suggestedLearners;
      return this;
    }

    public Builder withCredit(double credit)
    {
      this.credit = credit;
      return this;
    }

    public CourseDto build()
    {
      CourseDto courseDto = new CourseDto();
      courseDto.id = this.id;
      courseDto.courseCategoryId = this.courseCategoryId;
      courseDto.courseCategoryName = this.courseCategoryName;
      courseDto.type = this.type;
      courseDto.courseContentId = this.courseContentId;
      courseDto.authorId = this.authorId;
      courseDto.title = this.title;
      courseDto.description = this.description;
      courseDto.publishStatus = this.publishStatus;
      courseDto.hasQuiz = this.hasQuiz;
      courseDto.hasFeedback = this.hasFeedback;
      courseDto.hasAssessment = this.hasAssessment;
      courseDto.hasCertificate = this.hasCertificate;
      courseDto.thumbnailUrl = this.thumbnailUrl;
      courseDto.modulesCount = this.modulesCount;
      courseDto.interactionsCount = this.interactionsCount;
      courseDto.assessmentId = this.assessmentId;
      courseDto.certificateId = this.certificateId;
      courseDto.progress = this.progress;
      courseDto.properties = this.properties;
      courseDto.assignedDepartments = this.assignedDepartments;
      courseDto.assignedLearners = this.assignedLearners;
      courseDto.createdDate = this.createdDate;
      courseDto.modifiedDate = this.modifiedDate;
      courseDto.belongingDepartmentId = this.belongDepartmentId;
      courseDto.belongingDepartmentName = this.belongDepartmentName;
      courseDto.publishDate = this.publishDate;
      courseDto.enrollmentCount = this.enrollmentCount;
      courseDto.suggestedLearners = this.suggestedLearners;
      courseDto.assignedDepartmentNames = this.assignedDepartmentsEntity;
      courseDto.credit = this.credit;
      return courseDto;
    }
  }
}

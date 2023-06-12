package mn.erin.lms.base.domain.model.exam;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.Entity;
import mn.erin.lms.base.domain.model.category.ExamCategoryId;
import mn.erin.lms.base.domain.util.DateUtil;

/**
 * @author Galsan Bayart
 */
public class Exam implements Entity<Exam>
{
  private final ExamId id;
  private final String author;
  private final Date createdDate;
  private String name;
  private String description;
  private ExamPublishStatus examPublishStatus;
  private ExamStatus examStatus;
  private ExamCategoryId examCategoryId;
  private ExamGroupId examGroupId;
  private ExamType examType;
  private Date modifiedDate;
  private String modifiedUser;
  private List<HistoryOfModification> historyOfModifications;
  private ExamPublishConfig examPublishConfig;
  private ExamConfig examConfig;
  private Set<String> enrolledLearners;
  private Set<String> enrolledGroups;

  private Exam(ExamId id, String author, Date createdDate, String name, ExamStatus examStatus)
  {
    this.id = Objects.requireNonNull(id);
    this.author = Validate.notBlank(author);
    this.createdDate = Objects.requireNonNull(createdDate);
    this.name = Validate.notBlank(name);
    this.examStatus = examStatus;
  }

  public ExamId getId()
  {
    return id;
  }

  public String getAuthor()
  {
    return author;
  }

  public Date getCreatedDate()
  {
    return createdDate;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = Validate.notBlank(name);
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public ExamCategoryId getExamCategoryId()
  {
    return examCategoryId;
  }

  public void setExamCategoryId(ExamCategoryId examCategoryId)
  {
    this.examCategoryId = examCategoryId;
  }

  public ExamGroupId getExamGroupId()
  {
    return examGroupId;
  }

  public void setExamGroupId(ExamGroupId examGroupId)
  {
    this.examGroupId = examGroupId;
  }

  public ExamType getExamType()
  {
    return examType;
  }

  public void setExamType(ExamType examType)
  {
    this.examType = examType;
  }

  public Date getModifiedDate()
  {
    return modifiedDate;
  }

  public void setModifiedDate(Date modifiedDate)
  {
    this.modifiedDate = modifiedDate;
  }

  public String getModifiedUser()
  {
    return modifiedUser;
  }

  public void setModifiedUser(String modifiedUser)
  {
    this.modifiedUser = modifiedUser;
  }

  public List<HistoryOfModification> getHistoryOfModifications()
  {
    return historyOfModifications;
  }

  public void setHistoryOfModifications(List<HistoryOfModification> historyOfModifications)
  {
    this.historyOfModifications = historyOfModifications;
  }

  public void addModifyInfo(HistoryOfModification historyOfModification)
  {
    this.historyOfModifications.add(Objects.requireNonNull(historyOfModification));
  }

  public ExamPublishConfig getExamPublishConfig()
  {
    return examPublishConfig;
  }

  public void setExamPublishConfig(ExamPublishConfig examPublishConfig)
  {
    this.examPublishConfig = examPublishConfig;
  }

  public ExamConfig getExamConfig()
  {
    return examConfig;
  }

  public void setExamConfig(ExamConfig examConfig)
  {
    this.examConfig = examConfig;
  }

  @Override
  public boolean sameIdentityAs(Exam other)
  {
    return this.id.sameValueAs(other.id);
  }

  public ExamStatus getExamStatus()
  {
    return examStatus;
  }

  public void setExamStatus(ExamStatus examStatus)
  {
    this.examStatus = examStatus;
  }

  public Set<String> getEnrolledLearners()
  {
    return enrolledLearners;
  }

  public void setEnrolledLearners(Set<String> enrolledLearners)
  {
    this.enrolledLearners = enrolledLearners;
  }

  public Set<String> getEnrolledGroups()
  {
    return enrolledGroups;
  }

  public void setEnrolledGroups(Set<String> enrolledGroups)
  {
    this.enrolledGroups = enrolledGroups;
  }

  public boolean isFinished()
  {
    return this.examStatus == ExamStatus.FINISHED;
  }

  public Date getActualPublicationDate()
  {
    return DateUtil.combine(examPublishConfig.getPublishDate(), examPublishConfig.getPublishTime());
  }

  public Date getActualStartDate()
  {
    return DateUtil.combine(examConfig.getStartDate(), examConfig.getStartTime());
  }

  public Date getActualEndDate()
  {
    return DateUtil.combine(examConfig.getEndDate(), examConfig.getEndTime());
  }

  public ExamPublishStatus getExamPublishStatus()
  {
    return examPublishStatus;
  }

  public void setExamPublishStatus(ExamPublishStatus examPublishStatus)
  {
    this.examPublishStatus = examPublishStatus;
  }

  public static class Builder
  {
    private final ExamId id;
    private final String author;
    private final Date createdDate;
    private final String name;
    private String description;
    private ExamCategoryId examCategoryId;
    private ExamGroupId examGroupId;
    private ExamType examType;
    private ExamStatus examStatus;
    private ExamPublishStatus examPublishStatus;
    private Date modifiedDate;
    private String modifiedUser;
    private List<HistoryOfModification> historyOfModifications;
    private ExamPublishConfig examPublishConfig;
    private ExamConfig examConfig;
    private Set<String> enrolledLearners;
    private Set<String> enrolledGroups;

    public Builder(ExamId id, String author, Date createdDate, String name)
    {
      this.id = id;
      this.author = author;
      this.createdDate = createdDate;
      this.name = name;
    }

    public Builder withDescription(String description)
    {
      this.description = description;
      return this;
    }

    public Builder withExamStatus(ExamStatus examStatus)
    {
      this.examStatus = examStatus;
      return this;
    }

    public Builder withExamPublishStatus(ExamPublishStatus examPublishStatus)
    {
      this.examPublishStatus = examPublishStatus;
      return this;
    }

    public Builder withExamCategoryId(ExamCategoryId examCategoryId)
    {
      this.examCategoryId = examCategoryId;
      return this;
    }

    public Builder withExamGroupId(ExamGroupId examGroupId)
    {
      this.examGroupId = examGroupId;
      return this;
    }

    public Builder withExamType(ExamType examType)
    {
      this.examType = examType;
      return this;
    }

    public Builder withModifiedDate(Date modifiedDate)
    {
      this.modifiedDate = modifiedDate;
      return this;
    }

    public Builder withModifiedUser(String modifiedUser)
    {
      this.modifiedUser = modifiedUser;
      return this;
    }

    public Builder withHistoryOfModifications(List<HistoryOfModification> historyOfModifications)
    {
      this.historyOfModifications = historyOfModifications;
      return this;
    }

    public Builder withPublishConfig(ExamPublishConfig examPublishConfig)
    {
      this.examPublishConfig = examPublishConfig;
      return this;
    }

    public Builder withExamConfig(ExamConfig examConfig)
    {
      this.examConfig = examConfig;
      return this;
    }

    public Builder withEnrolledLearners(Set<String> enrolledLearners)
    {
      this.enrolledLearners = enrolledLearners;
      return this;
    }

    public Builder withEnrolledGroups(Set<String> enrolledGroups)
    {
      this.enrolledGroups = enrolledGroups;
      return this;
    }

    public Exam build()
    {
      Exam exam = new Exam(this.id, this.author, this.createdDate, this.name, this.examStatus);
      exam.description = this.description;
      exam.examCategoryId = this.examCategoryId;
      exam.examGroupId = this.examGroupId;
      exam.examType = this.examType;
      exam.modifiedDate = this.modifiedDate;
      exam.modifiedUser = this.modifiedUser;
      exam.examPublishStatus = this.examPublishStatus;
      exam.examStatus = this.examStatus;
      exam.historyOfModifications = this.historyOfModifications;
      exam.examPublishConfig = this.examPublishConfig;
      exam.examConfig = this.examConfig;
      exam.enrolledLearners = this.enrolledLearners;
      exam.enrolledGroups = this.enrolledGroups;
      return exam;
    }
  }
}

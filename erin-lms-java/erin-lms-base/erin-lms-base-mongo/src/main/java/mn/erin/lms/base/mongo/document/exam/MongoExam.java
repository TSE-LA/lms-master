package mn.erin.lms.base.mongo.document.exam;

import java.util.*;

import org.apache.commons.lang3.Validate;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Galsan Bayart
 */
@Document
public class MongoExam
{
  private String id;
  private String author;
  private Date createdDate;
  private String name;
  private String description;
  private String examStatus;
  private String examPublishStatus;
  private String examCategoryId;
  private String examGroupId;
  private String examType;
  private Date modifiedDate;
  private String modifiedUser;
  private List<MongoHistoryOfModification> historyOfModifications = new ArrayList<>();
  private MongoExamPublishConfig publishConfig;
  private MongoExamConfig mongoExamConfig;
  private Set<String> enrolledLearners;
  private Set<String> enrolledGroups;

  public MongoExam()
  {
    /*Mongo need an empty constructor*/
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = Validate.notBlank(id);
  }

  public String getAuthor()
  {
    return author;
  }

  public void setAuthor(String author)
  {
    this.author = Validate.notBlank(author);
  }

  public Date getCreatedDate()
  {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate)
  {
    this.createdDate = Objects.requireNonNull(createdDate);
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

  public String getExamCategoryId()
  {
    return examCategoryId;
  }

  public void setExamCategoryId(String examCategoryId)
  {
    this.examCategoryId = Validate.notBlank(examCategoryId);
  }

  public String getExamGroupId()
  {
    return examGroupId;
  }

  public void setExamGroupId(String examGroupId)
  {
    this.examGroupId = examGroupId;
  }

  public String getExamType()
  {
    return examType;
  }

  public void setExamType(String examType)
  {
    this.examType = Validate.notBlank(examType);
  }

  public Date getModifiedDate()
  {
    return modifiedDate;
  }

  public void setModifiedDate(Date modifiedDate)
  {
    this.modifiedDate = Objects.requireNonNull(modifiedDate);
  }

  public String getModifiedUser()
  {
    return modifiedUser;
  }

  public void setModifiedUser(String modifiedUser)
  {
    this.modifiedUser = Validate.notBlank(modifiedUser);
  }

  public List<MongoHistoryOfModification> getHistoryOfModifications()
  {
    return historyOfModifications;
  }

  public void setHistoryOfModifications(List<MongoHistoryOfModification> historyOfModifications)
  {
    this.historyOfModifications = Objects.requireNonNull(historyOfModifications);
  }

  public void addModifyInfo(MongoHistoryOfModification historyOfModification)
  {
    this.historyOfModifications.add(Objects.requireNonNull(historyOfModification));
  }

  public MongoExamPublishConfig getPublishConfig()
  {
    return publishConfig;
  }

  public void setPublishConfig(MongoExamPublishConfig publishConfig)
  {
    this.publishConfig = Objects.requireNonNull(publishConfig);
  }

  public MongoExamConfig getMongoExamConfig()
  {
    return mongoExamConfig;
  }

  public void setMongoExamConfig(MongoExamConfig mongoExamConfig)
  {
    this.mongoExamConfig = Objects.requireNonNull(mongoExamConfig);
  }

  public String getExamStatus() {
    return examStatus;
  }

  public void setExamStatus(String examStatus) {
    this.examStatus = examStatus;
  }

  public Set<String> getEnrolledLearners() {
    return enrolledLearners;
  }

  public void setEnrolledLearners(Set<String> enrolledLearners) {
    this.enrolledLearners = enrolledLearners;
  }

  public Set<String> getEnrolledGroups() {
    return enrolledGroups;
  }

  public void setEnrolledGroups(Set<String> enrolledGroups) {
    this.enrolledGroups = enrolledGroups;
  }

  public String getExamPublishStatus()
  {
    return examPublishStatus;
  }

  public void setExamPublishStatus(String examPublishStatus)
  {
    this.examPublishStatus = examPublishStatus;
  }
}

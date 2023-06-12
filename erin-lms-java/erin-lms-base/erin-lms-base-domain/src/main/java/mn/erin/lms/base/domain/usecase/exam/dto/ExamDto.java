package mn.erin.lms.base.domain.usecase.exam.dto;

import java.util.Date;
import java.util.Set;

import mn.erin.lms.base.domain.model.exam.ExamConfig;
import mn.erin.lms.base.domain.model.exam.ExamPublishConfig;

/**
 * @author Galsan Bayart.
 */
public class ExamDto
{
  private String id;
  private String name;
  private String description;
  private String categoryId;
  private String groupId;
  private String examType;
  private String publishState;
  private String examStatus;
  private ExamPublishConfig publishConfig;
  private ExamConfig examConfigure;
  private Set<String> enrolledLearners;
  private Set<String> enrolledGroups;
  private Date createdDate;
  private Date modifiedDate;
  private String modifiedUser;

  public ExamDto(String id, String name)
  {
    this.id = id;
    this.name = name;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public String getCategoryId()
  {
    return categoryId;
  }

  public void setCategoryId(String categoryId)
  {
    this.categoryId = categoryId;
  }

  public String getExamType()
  {
    return examType;
  }

  public void setExamType(String examType)
  {
    this.examType = examType;
  }

  public ExamPublishConfig getPublishConfig()
  {
    return publishConfig;
  }

  public void setPublishConfig(ExamPublishConfig publishConfig)
  {
    this.publishConfig = publishConfig;
  }

  public ExamConfig getExamConfigure()
  {
    return examConfigure;
  }

  public void setExamConfigure(ExamConfig examConfigure)
  {
    this.examConfigure = examConfigure;
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

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String groupId)
  {
    this.groupId = groupId;
  }

  public Date getCreatedDate()
  {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate)
  {
    this.createdDate = createdDate;
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

  public String getPublishState()
  {
    return publishState;
  }

  public void setPublishState(String publishState)
  {
    this.publishState = publishState;
  }

  public String getExamStatus()
  {
    return examStatus;
  }

  public void setExamStatus(String examStatus)
  {
    this.examStatus = examStatus;
  }
}

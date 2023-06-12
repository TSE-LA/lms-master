package mn.erin.lms.base.rest.model.exam;

import java.util.Set;

/**
 * @author Galsan Bayart.
 */

public class RestExam
{
  private String id;
  private String name;
  private String description;
  private String categoryId;
  private String groupId;
  private String examType;
  private RestExamPublishConfig publishConfig;
  private RestExamConfigure examConfigure;
  private Set<String> enrolledLearners;
  private Set<String> enrolledGroups;

  public RestExam()
  {
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

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String groupId)
  {
    this.groupId = groupId;
  }

  public String getExamType()
  {
    return examType;
  }

  public void setExamType(String examType)
  {
    this.examType = examType;
  }

  public RestExamPublishConfig getPublishConfig()
  {
    return publishConfig;
  }

  public void setPublishConfig(RestExamPublishConfig publishConfig)
  {
    this.publishConfig = publishConfig;
  }

  public RestExamConfigure getExamConfigure()
  {
    return examConfigure;
  }

  public void setExamConfigure(RestExamConfigure examConfigure)
  {
    this.examConfigure = examConfigure;
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
}

package mn.erin.lms.base.domain.usecase.announcement.dto;

import java.util.Set;

import mn.erin.lms.base.domain.model.course.PublishStatus;

public class UpdateAnnouncementInput
{
  private String id;
  private String modifiedUser;
  private String title;
  private String content;
  private PublishStatus publishedStatus;
  private Set<String> departmentIds;

  public UpdateAnnouncementInput()
  {
  }

  public UpdateAnnouncementInput(String id, String modifiedUser, String title, String content, Set<String> departmentIds)
  {
    this.id = id;
    this.modifiedUser = modifiedUser;
    this.title = title;
    this.content = content;
    this.departmentIds = departmentIds;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getModifiedUser()
  {
    return modifiedUser;
  }

  public void setModifiedUser(String modifiedUser)
  {
    this.modifiedUser = modifiedUser;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getContent()
  {
    return content;
  }

  public void setContent(String content)
  {
    this.content = content;
  }

  public Set<String> getDepartmentIds()
  {
    return departmentIds;
  }

  public void setDepartmentIds(Set<String> departmentIds)
  {
    this.departmentIds = departmentIds;
  }

  public PublishStatus getPublishedStatus()
  {
    return publishedStatus;
  }

  public void setPublishedStatus(PublishStatus publishedStatus)
  {
    this.publishedStatus = publishedStatus;
  }
}

package mn.erin.lms.base.domain.usecase.announcement.dto;

import java.util.Set;

import mn.erin.lms.base.domain.model.course.PublishStatus;

public class AnnouncementDto
{
  private String announcementId;
  private String author;
  private String createdDate;
  private String modifiedUser;
  private String modifiedDate;
  private String title;
  private String content;
  private Set<String> departmentIds;
  private PublishStatus publishStatus = PublishStatus.UNPUBLISHED;

  public AnnouncementDto(String announcementId, String author, String createdDate, String modifiedUser,
      String modifiedDate, String title, String content, Set<String> departmentIds,
      PublishStatus publishStatus)
  {
    this.announcementId = announcementId;
    this.author = author;
    this.createdDate = createdDate;
    this.modifiedUser = modifiedUser;
    this.modifiedDate = modifiedDate;
    this.title = title;
    this.content = content;
    this.departmentIds = departmentIds;
    this.publishStatus = publishStatus;
  }

  public String getAnnouncementId()
  {
    return announcementId;
  }

  public void setAnnouncementId(String announcementId)
  {
    this.announcementId = announcementId;
  }

  public String getAuthor()
  {
    return author;
  }

  public void setAuthor(String author)
  {
    this.author = author;
  }

  public String getCreatedDate()
  {
    return createdDate;
  }

  public void setCreatedDate(String createdDate)
  {
    this.createdDate = createdDate;
  }

  public String getModifiedUser()
  {
    return modifiedUser;
  }

  public void setModifiedUser(String modifiedUser)
  {
    this.modifiedUser = modifiedUser;
  }

  public String getModifiedDate()
  {
    return modifiedDate;
  }

  public void setModifiedDate(String modifiedDate)
  {
    this.modifiedDate = modifiedDate;
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

  public PublishStatus getPublishStatus()
  {
    return publishStatus;
  }

  public void setPublishStatus(PublishStatus publishStatus)
  {
    this.publishStatus = publishStatus;
  }
}

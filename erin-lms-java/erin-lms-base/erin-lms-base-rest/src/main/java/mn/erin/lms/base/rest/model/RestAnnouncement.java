package mn.erin.lms.base.rest.model;

import java.util.Set;

public class RestAnnouncement
{
  private String announcementId;
  private String author;
  private String modifiedUser;
  private String title;
  private String content;
  private Set<String> departmentIds;

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

  public Set<String> getDepartmentIds()
  {
    return departmentIds;
  }

  public void setDepartmentIds(Set<String> departmentIds)
  {
    this.departmentIds = departmentIds;
  }

  public String getContent()
  {
    return content;
  }

  public void setContent(String content)
  {
    this.content = content;
  }
}

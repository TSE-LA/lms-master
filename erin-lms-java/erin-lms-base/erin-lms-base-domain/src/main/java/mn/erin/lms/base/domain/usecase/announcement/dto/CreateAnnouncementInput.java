package mn.erin.lms.base.domain.usecase.announcement.dto;

import java.util.Set;

public class CreateAnnouncementInput
{
  private String author;
  private String title;
  private String content;
  private Set<String> departmentIds;

  public CreateAnnouncementInput(String author, String title, String content, Set<String> departmentIds)
  {
    this.author = author;
    this.title = title;
    this.departmentIds = departmentIds;
    this.content = content;
  }

  public String getAuthor()
  {
    return author;
  }

  public void setAuthor(String author)
  {
    this.author = author;
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
}

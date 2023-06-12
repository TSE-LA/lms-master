package mn.erin.lms.base.mongo.document.announcement;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import mn.erin.lms.base.mongo.document.course.MongoPublishStatus;

@Document
public class MongoAnnouncement
{
  @Id
  private String id;

  private String author;
  private LocalDateTime createdDate;
  private String modifiedUser;
  private LocalDateTime modifiedDate;
  private String title;
  private String content;
  private Set<String> departmentIds = new HashSet<>();
  private MongoPublishStatus publishStatus;

  public MongoAnnouncement(String id, String author, LocalDateTime createdDate, LocalDateTime modifiedDate,
      String title, String content, Set<String> departmentIds, MongoPublishStatus publishStatus)
  {
    this.id = Validate.notBlank(id);
    this.author = Validate.notBlank(author);
    this.createdDate = Validate.notNull(createdDate);
    this.modifiedDate = modifiedDate;
    this.title = Validate.notBlank(title);
    this.content = Validate.notBlank(content);
    this.departmentIds = departmentIds;
    this.publishStatus = Validate.notNull(publishStatus);
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getAuthor()
  {
    return author;
  }

  public void setAuthor(String author)
  {
    this.author = author;
  }

  public LocalDateTime getCreatedDate()
  {
    return createdDate;
  }

  public void setCreatedDate(LocalDateTime createdDate)
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

  public LocalDateTime getModifiedDate()
  {
    return modifiedDate;
  }

  public void setModifiedDate(LocalDateTime modifiedDate)
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

  public MongoPublishStatus getPublishStatus()
  {
    return publishStatus;
  }

  public void setPublishStatus(MongoPublishStatus publishStatus)
  {
    this.publishStatus = publishStatus;
  }
}

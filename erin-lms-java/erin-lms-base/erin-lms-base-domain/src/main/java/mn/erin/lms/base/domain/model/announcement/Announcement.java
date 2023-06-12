package mn.erin.lms.base.domain.model.announcement;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import javax.validation.constraints.NotNull;

import mn.erin.domain.base.model.Entity;
import mn.erin.lms.base.domain.model.course.PublishStatus;

public class Announcement implements Entity<Announcement>, Comparable<Announcement>
{
  private final AnnouncementId announcementId;
  private final String author;
  private final LocalDateTime createdDate;
  private String modifiedUser;
  private LocalDateTime modifiedDate;
  private String title;
  private String content;
  private Set<String> departmentIds;
  private PublishStatus publishStatus = PublishStatus.UNPUBLISHED;

  public Announcement(AnnouncementId announcementId, String author, String modifiedUser, LocalDateTime createdDate,
      LocalDateTime modifiedDate, String title, String content, Set<String> departmentIds)
  {
    this.announcementId = Objects.requireNonNull(announcementId, "Announcement Id cannot be null!");
    this.author = author;
    this.modifiedUser = modifiedUser;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.title = title;
    this.content = content;
    this.departmentIds = departmentIds;
  }

  public Announcement(AnnouncementId announcementId, String author, String modifiedUser, LocalDateTime createdDate,
      LocalDateTime modifiedDate, String title, String content, Set<String> groupIds, PublishStatus publishStatus)
  {
    this.announcementId = announcementId;
    this.author = author;
    this.modifiedUser = modifiedUser;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.title = title;
    this.content = content;
    this.departmentIds = groupIds;
    this.publishStatus = publishStatus;
  }

  public AnnouncementId getAnnouncementId()
  {
    return announcementId;
  }

  public String getAuthor()
  {
    return author;
  }

  public LocalDateTime getCreatedDate()
  {
    return createdDate;
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

  public PublishStatus getPublishStatus()
  {
    return publishStatus;
  }

  public void setPublishStatus(PublishStatus publishStatus)
  {
    this.publishStatus = publishStatus;
  }

  @Override
  public int compareTo(@NotNull Announcement other)
  {
    return modifiedDate.compareTo(other.modifiedDate);
  }

  @Override
  public boolean sameIdentityAs(Announcement other)
  {
    return this.announcementId.equals(other.announcementId);
  }
}

package mn.erin.lms.base.mongo.document.announcement;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class MongoAnnouncementRuntime
{
  @Id
  private MongoAnnouncementRuntimeId id;
  private MongoAnnouncementRuntimeStatus announcementRuntimeStatus;
  private LocalDateTime viewedDate;

  public MongoAnnouncementRuntime(MongoAnnouncementRuntimeId id,
      MongoAnnouncementRuntimeStatus announcementRuntimeStatus, LocalDateTime viewedDate)
  {
    this.id = id;
    this.announcementRuntimeStatus = announcementRuntimeStatus;
    this.viewedDate = viewedDate;
  }

  public MongoAnnouncementRuntimeId getId()
  {
    return id;
  }

  public void setId(MongoAnnouncementRuntimeId id)
  {
    this.id = id;
  }

  public MongoAnnouncementRuntimeStatus getAnnouncementRuntimeStatus()
  {
    return announcementRuntimeStatus;
  }

  public void setAnnouncementRuntimeStatus(MongoAnnouncementRuntimeStatus announcementRuntimeStatus)
  {
    this.announcementRuntimeStatus = announcementRuntimeStatus;
  }

  public LocalDateTime getViewedDate()
  {
    return viewedDate;
  }

  public void setViewedDate(LocalDateTime viewedDate)
  {
    this.viewedDate = viewedDate;
  }
}

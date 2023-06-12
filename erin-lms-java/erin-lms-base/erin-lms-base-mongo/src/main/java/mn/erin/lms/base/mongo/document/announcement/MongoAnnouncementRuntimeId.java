package mn.erin.lms.base.mongo.document.announcement;

public class MongoAnnouncementRuntimeId
{
  private String announcementId;
  private String learnerId;

  public MongoAnnouncementRuntimeId(String announcementId, String learnerId)
  {
    this.announcementId = announcementId;
    this.learnerId = learnerId;
  }

  public String getAnnouncementId()
  {
    return announcementId;
  }

  public String getLearnerId()
  {
    return learnerId;
  }
}

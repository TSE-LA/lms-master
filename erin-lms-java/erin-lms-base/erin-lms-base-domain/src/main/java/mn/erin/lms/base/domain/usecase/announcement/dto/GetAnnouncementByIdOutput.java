package mn.erin.lms.base.domain.usecase.announcement.dto;

public class GetAnnouncementByIdOutput
{
  private final AnnouncementDto announcement;

  public GetAnnouncementByIdOutput(AnnouncementDto announcement)
  {
    this.announcement = announcement;
  }

  public AnnouncementDto getAnnouncement()
  {
    return announcement;
  }
}

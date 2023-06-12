package mn.erin.lms.base.domain.usecase.announcement.dto;

import java.util.List;

public class GetAllAnnouncementsOutput
{
  private List<AnnouncementDto> announcements;

  public GetAllAnnouncementsOutput(List<AnnouncementDto> announcements)
  {
    this.announcements = announcements;
  }

  public List<AnnouncementDto> getAnnouncements()
  {
    return announcements;
  }
}

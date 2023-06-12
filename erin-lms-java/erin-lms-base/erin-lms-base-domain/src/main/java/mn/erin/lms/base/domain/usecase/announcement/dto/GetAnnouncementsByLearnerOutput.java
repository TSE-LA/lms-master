package mn.erin.lms.base.domain.usecase.announcement.dto;

import java.util.List;
import java.util.Map;

public class GetAnnouncementsByLearnerOutput
{
  private List<Map<String, Object>> announcements;

  public GetAnnouncementsByLearnerOutput(List<Map<String, Object>> announcements)
  {
    this.announcements = announcements;
  }

  public List<Map<String, Object>> getAnnouncements()
  {
    return announcements;
  }
}

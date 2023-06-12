package mn.erin.lms.base.domain.usecase.announcement.dto;

import java.util.List;

import mn.erin.lms.base.domain.model.announcement.AnnouncementRuntime;

public class GetNewAnnouncementsByLearnerOutput
{
  private List<AnnouncementRuntime> announcementRuntimeList;

  public GetNewAnnouncementsByLearnerOutput(List<AnnouncementRuntime> announcementRuntimeList)
  {
    this.announcementRuntimeList = announcementRuntimeList;
  }

  public List<AnnouncementRuntime> getAnnouncementRuntimeList()
  {
    return announcementRuntimeList;
  }
}

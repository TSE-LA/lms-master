package mn.erin.lms.base.domain.usecase.announcement.dto;

import mn.erin.lms.base.domain.model.announcement.AnnouncementRuntime;

public class GetAnnouncementRuntimeByIdOutput
{
  private AnnouncementRuntime announcementRuntime;

  public GetAnnouncementRuntimeByIdOutput(AnnouncementRuntime announcementRuntime)
  {
    this.announcementRuntime = announcementRuntime;
  }

  public AnnouncementRuntime getAnnouncementRuntime()
  {
    return announcementRuntime;
  }
}

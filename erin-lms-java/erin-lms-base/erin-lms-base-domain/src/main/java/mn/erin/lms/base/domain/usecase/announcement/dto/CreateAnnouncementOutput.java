package mn.erin.lms.base.domain.usecase.announcement.dto;

import org.apache.commons.lang3.Validate;

public class CreateAnnouncementOutput
{
  private final String announcementId;

  public CreateAnnouncementOutput(String announcementId)
  {
    this.announcementId = Validate.notBlank(announcementId);
  }

  public String getAnnouncementId()
  {
    return announcementId;
  }
}

package mn.erin.lms.base.domain.model.announcement;

import mn.erin.domain.base.model.EntityId;

public class AnnouncementId extends EntityId
{
  public AnnouncementId(String id)
  {
    super(id);
  }

  public static AnnouncementId valueOf(String id)
  {
    return new AnnouncementId(id);
  }
}

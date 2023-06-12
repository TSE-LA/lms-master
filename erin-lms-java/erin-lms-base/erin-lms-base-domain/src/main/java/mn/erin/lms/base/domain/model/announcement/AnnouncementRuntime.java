package mn.erin.lms.base.domain.model.announcement;

import java.time.LocalDateTime;

import mn.erin.domain.base.model.Entity;

public class AnnouncementRuntime implements Entity<AnnouncementRuntime>
{
  private final AnnouncementId announcementId;
  private final String learnerId;
  private AnnouncementRuntimeStatus state;
  private LocalDateTime viewedDate;

  public AnnouncementRuntime(AnnouncementId announcementId, String leranerId, AnnouncementRuntimeStatus state, LocalDateTime viewedDate)
  {
    this.announcementId = announcementId;
    this.learnerId = leranerId;
    this.state = state;
    this.viewedDate = viewedDate;
  }

  public AnnouncementId getAnnouncementId()
  {
    return announcementId;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public AnnouncementRuntimeStatus getState()
  {
    return state;
  }

  public void setState(AnnouncementRuntimeStatus state)
  {
    this.state = state;
  }

  public LocalDateTime getViewedDate()
  {
    return viewedDate;
  }

  public void setViewedDate(LocalDateTime viewedDate)
  {
    this.viewedDate = viewedDate;
  }

  @Override
  public boolean sameIdentityAs(AnnouncementRuntime other)
  {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'sameIdentityAs'");
  }
}

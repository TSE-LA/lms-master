package mn.erin.lms.base.domain.model.course;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class DateInfo implements Serializable
{
  private static final long serialVersionUID = -7135724066731691385L;

  private LocalDateTime createdDate;
  private LocalDateTime modifiedDate;
  private LocalDateTime publishDate;

  public LocalDateTime getCreatedDate()
  {
    return createdDate;
  }

  public void setCreatedDate(LocalDateTime createdDate)
  {
    this.createdDate = createdDate;
  }

  public LocalDateTime getModifiedDate()
  {
    return modifiedDate;
  }

  public void setModifiedDate(LocalDateTime modifiedDate)
  {
    this.modifiedDate = modifiedDate;
  }

  public LocalDateTime getPublishDate()
  {
    return publishDate;
  }

  public void setPublishDate(LocalDateTime publishDate)
  {
    this.publishDate = publishDate;
  }
}

package mn.erin.lms.base.domain.usecase.announcement;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

import mn.erin.common.datetime.DateTimeUtils;
import mn.erin.lms.base.domain.model.announcement.Announcement;
import mn.erin.lms.base.domain.usecase.announcement.dto.AnnouncementDto;

public abstract class AnnouncementUseCase
{
  protected AnnouncementDto toAnnouncementDto(Announcement announcement)
  {
    String createdDate = DateTimeUtils.toShortIsoString(toDate(announcement.getCreatedDate()));
    String modifiedDate = DateTimeUtils.toShortIsoString(toDate(announcement.getModifiedDate()));

    return new AnnouncementDto(
        announcement.getAnnouncementId().getId(),
        announcement.getAuthor(),
        createdDate,
        announcement.getModifiedUser(),
        modifiedDate,
        announcement.getTitle(),
        announcement.getContent(),
        announcement.getDepartmentIds(),
        announcement.getPublishStatus()
    );
  }

  private Date toDate(LocalDateTime localDateTime)
  {
    return Timestamp.valueOf(localDateTime);
  }
}

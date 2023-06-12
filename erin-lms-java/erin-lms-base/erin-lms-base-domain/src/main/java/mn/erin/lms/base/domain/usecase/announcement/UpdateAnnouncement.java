package mn.erin.lms.base.domain.usecase.announcement;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.announcement.Announcement;
import mn.erin.lms.base.domain.repository.AnnouncementRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.usecase.announcement.dto.UpdateAnnouncementInput;

@Authorized(users = { Author.class, Instructor.class })
public class UpdateAnnouncement implements UseCase<UpdateAnnouncementInput, Boolean>
{
  private final AnnouncementRepository announcementRepository;

  public UpdateAnnouncement(AnnouncementRepository announcementRepository)
  {
    this.announcementRepository = Objects.requireNonNull(announcementRepository);
  }

  @Override
  public Boolean execute(UpdateAnnouncementInput input) throws UseCaseException
  {
    Validate.notNull(input);

    try
    {
      Announcement announcement = this.announcementRepository.getById(input.getId());

      if (!StringUtils.isBlank(input.getModifiedUser()))
      {
        announcement.setModifiedUser(input.getModifiedUser());
      }

      if (!StringUtils.isBlank(input.getTitle()))
      {
        announcement.setTitle(input.getTitle());
      }

      if (!StringUtils.isBlank(input.getContent()))
      {
        announcement.setContent(input.getContent());
      }

      if (input.getDepartmentIds() != null && !input.getDepartmentIds().isEmpty())
      {
        announcement.setDepartmentIds(input.getDepartmentIds());
      }

      if (input.getPublishedStatus() != null)
      {
        announcement.setPublishStatus(input.getPublishedStatus());
      }

      announcement.setModifiedDate(LocalDateTime.now(ZoneId.of("Asia/Ulaanbaatar")));
      return this.announcementRepository.update(announcement);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

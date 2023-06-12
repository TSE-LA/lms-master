package mn.erin.lms.base.domain.usecase.announcement;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.model.announcement.Announcement;
import mn.erin.lms.base.domain.model.course.PublishStatus;
import mn.erin.lms.base.domain.repository.AnnouncementRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.usecase.announcement.dto.CreateAnnouncementInput;
import mn.erin.lms.base.domain.usecase.announcement.dto.CreateAnnouncementOutput;

@Authorized(users = { Author.class, Instructor.class })
public class CreateAnnouncement implements UseCase<CreateAnnouncementInput, CreateAnnouncementOutput>
{
  private final AnnouncementRepository announcementRepository;

  public CreateAnnouncement(AnnouncementRepository announcementRepository)
  {
    this.announcementRepository = Objects.requireNonNull(announcementRepository);
  }

  @Override
  public CreateAnnouncementOutput execute(CreateAnnouncementInput input) throws UseCaseException
  {
    Validate.notNull(input);
    try
    {
      Announcement announcement = this.announcementRepository.create(input.getAuthor(), input.getTitle(), input.getContent(), input.getDepartmentIds(),
          PublishStatus.UNPUBLISHED);
      return new CreateAnnouncementOutput(announcement.getAnnouncementId().getId());
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

package mn.erin.lms.base.domain.usecase.announcement;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.repository.AnnouncementRepository;
import mn.erin.lms.base.domain.repository.AnnouncementRuntimeRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;

@Authorized(users = { Author.class, Instructor.class })
public class DeleteAnnouncement implements UseCase<String, Boolean>
{
  private final AnnouncementRepository announcementRepository;
  private final AnnouncementRuntimeRepository announcementRuntimeRepository;

  public DeleteAnnouncement(AnnouncementRepository announcementRepository, AnnouncementRuntimeRepository announcementRuntimeRepository)
  {
    this.announcementRepository = Objects.requireNonNull(announcementRepository);
    this.announcementRuntimeRepository = Objects.requireNonNull(announcementRuntimeRepository);
  }

  @Override
  public Boolean execute(String input) throws UseCaseException
  {
    Validate.notNull(input);
    try
    {

      this.announcementRuntimeRepository.deleteAllByAnnouncementId(input);

      return this.announcementRepository.delete(input);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

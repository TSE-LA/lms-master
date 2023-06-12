package mn.erin.lms.base.domain.usecase.announcement;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.repository.AnnouncementRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.usecase.announcement.dto.AnnouncementDto;
import mn.erin.lms.base.domain.usecase.announcement.dto.GetAnnouncementByIdOutput;

public class GetAnnouncementById extends AnnouncementUseCase implements UseCase<String, GetAnnouncementByIdOutput>
{
  private final AnnouncementRepository announcementRepository;

  public GetAnnouncementById(AnnouncementRepository announcementRepository)
  {
    this.announcementRepository = Objects.requireNonNull(announcementRepository);
  }

  @Override
  public GetAnnouncementByIdOutput execute(String input) throws UseCaseException
  {
    Validate.notNull(input);

    try
    {
      AnnouncementDto announcement = this.toAnnouncementDto(this.announcementRepository.getById(input));
      return new GetAnnouncementByIdOutput(announcement);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

package mn.erin.lms.base.domain.usecase.announcement;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.domain.model.announcement.AnnouncementRuntime;
import mn.erin.lms.base.domain.repository.AnnouncementRuntimeRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.usecase.announcement.dto.GetAnnouncementRuntimeByIdOutput;

public class GetAnnouncementRuntimeById implements UseCase<String, GetAnnouncementRuntimeByIdOutput>
{
  private final AnnouncementRuntimeRepository announcementRuntimeRepository;
  private final AccessIdentityManagement accessIdentityManagement;

  public GetAnnouncementRuntimeById(AnnouncementRuntimeRepository announcementRuntimeRepository,
      AccessIdentityManagement accessIdentityManagement)
  {
    this.announcementRuntimeRepository = Objects.requireNonNull(announcementRuntimeRepository);
    this.accessIdentityManagement = Objects.requireNonNull(accessIdentityManagement);
  }

  @Override
  public GetAnnouncementRuntimeByIdOutput execute(String input) throws UseCaseException
  {
    Validate.notBlank(input);

    try
    {
      String username = this.accessIdentityManagement.getCurrentUsername();
      AnnouncementRuntime announcementRuntime = this.announcementRuntimeRepository.findById(input, username);
      return new GetAnnouncementRuntimeByIdOutput(announcementRuntime);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

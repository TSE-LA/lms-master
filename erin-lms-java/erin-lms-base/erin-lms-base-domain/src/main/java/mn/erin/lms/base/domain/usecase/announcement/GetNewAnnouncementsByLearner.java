package mn.erin.lms.base.domain.usecase.announcement;

import java.util.List;
import java.util.Objects;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.announcement.AnnouncementRuntime;
import mn.erin.lms.base.domain.model.announcement.AnnouncementRuntimeStatus;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.announcement.dto.GetNewAnnouncementsByLearnerOutput;

public class GetNewAnnouncementsByLearner implements UseCase<Void, GetNewAnnouncementsByLearnerOutput>
{
  private final LmsRepositoryRegistry lmsRepositoryRegistry;
  private final LmsServiceRegistry lmsServiceRegistry;

  public GetNewAnnouncementsByLearner(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    this.lmsRepositoryRegistry = Objects.requireNonNull(lmsRepositoryRegistry);
    this.lmsServiceRegistry = Objects.requireNonNull(lmsServiceRegistry);
  }

  @Override
  public GetNewAnnouncementsByLearnerOutput execute(Void input) throws UseCaseException
  {
    String username = this.lmsServiceRegistry.getAccessIdentityManagement().getCurrentUsername();
    try
    {
      List<AnnouncementRuntime> announcementRuntimeList = this.lmsRepositoryRegistry.getAnnouncementRuntimeRepository()
          .findByLearnerIdAndStatus(username, AnnouncementRuntimeStatus.NEW);
      return new GetNewAnnouncementsByLearnerOutput(announcementRuntimeList);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

package mn.erin.lms.base.domain.usecase.course;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.dms.repository.DMSRepositoryException;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsFileSystemService;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;

@Authorized(users = { Instructor.class })
public class GetSCORMFolderId implements UseCase<String, String>
{
  private final LmsRepositoryRegistry lmsRepositoryRegistry;
  private final LmsServiceRegistry lmsServiceRegistry;

  public GetSCORMFolderId(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    this.lmsRepositoryRegistry = lmsRepositoryRegistry;
    this.lmsServiceRegistry = lmsServiceRegistry;
  }

  @Override
  public String execute(String folderId) throws UseCaseException
  {
    LmsFileSystemService lmsFileSystemService = lmsServiceRegistry.getLmsFileSystemService();
    try
    {
      return lmsFileSystemService.getSCORMFolderId(lmsFileSystemService.getCourseFolderId(folderId));
    }
    catch (DMSRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

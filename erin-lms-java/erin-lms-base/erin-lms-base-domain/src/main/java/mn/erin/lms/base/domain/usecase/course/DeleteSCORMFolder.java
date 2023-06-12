package mn.erin.lms.base.domain.usecase.course;


import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsFileSystemService;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
public class DeleteSCORMFolder
{
  private final LmsRepositoryRegistry lmsRepositoryRegistry;
  private final LmsServiceRegistry lmsServiceRegistry;

  public DeleteSCORMFolder(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    this.lmsRepositoryRegistry = lmsRepositoryRegistry;
    this.lmsServiceRegistry = lmsServiceRegistry;
  }

  public boolean execute(String folderId) throws UseCaseException
  {
    LmsFileSystemService lmsFileSystemService = lmsServiceRegistry.getLmsFileSystemService();
    return lmsFileSystemService.deleteFolder(folderId);
  }
}

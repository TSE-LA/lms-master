package mn.erin.lms.base.domain.usecase.certificate;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.dms.repository.DMSRepositoryException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsFileSystemService;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.LmsUseCase;

/**
 * @author Oyungerel Chuluunsukh.
 */
@Authorized(users = { Author.class })
public class DeleteLearnerCertificate extends LmsUseCase<String, Boolean>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteLearnerCertificate.class);

  public DeleteLearnerCertificate(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  protected Boolean executeImpl(String input) throws UseCaseException
  {
    Validate.notBlank(input);
    CourseId courseId = CourseId.valueOf(input);
    boolean isDeleted = lmsRepositoryRegistry.getLearnerCertificateRepository().deleteByCourseId(courseId);
    if (isDeleted)
    {
      LmsFileSystemService lmsFileSystemService = lmsServiceRegistry.getLmsFileSystemService();
      try
      {
        String folderId = lmsFileSystemService.getLearnerCertificateFolder(input);
        return lmsFileSystemService.deleteFolder(folderId);
      }
      catch (DMSRepositoryException e)
      {
        LOGGER.warn("No certificate folder found on courseId: [{}]", input);
        return true;
      }
    }
    return false;
  }
}

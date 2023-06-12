package mn.erin.lms.jarvis.server.registry;

import javax.inject.Inject;

import mn.erin.lms.base.scorm.repository.RuntimeDataRepository;
import mn.erin.lms.base.scorm.repository.SCORMContentRepository;
import mn.erin.lms.base.scorm.repository.SCORMQuizRepository;
import mn.erin.lms.base.scorm.repository.SCORMRepositoryRegistry;
import mn.erin.lms.base.scorm.repository.SCORMWrapperRepository;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class JarvisSCORMRepositoryRegistry implements SCORMRepositoryRegistry
{
  private SCORMContentRepository scormContentRepository;
  private SCORMQuizRepository scormQuizRepository;
  private SCORMWrapperRepository scormWrapperRepository;
  private RuntimeDataRepository runtimeDataRepository;

  @Inject
  public void setScormContentRepository(SCORMContentRepository scormContentRepository)
  {
    this.scormContentRepository = scormContentRepository;
  }

  @Inject
  public void setScormQuizRepository(SCORMQuizRepository scormQuizRepository)
  {
    this.scormQuizRepository = scormQuizRepository;
  }

  @Inject
  public void setScormWrapperRepository(SCORMWrapperRepository scormWrapperRepository)
  {
    this.scormWrapperRepository = scormWrapperRepository;
  }

  @Inject
  public void setRuntimeDataRepository(RuntimeDataRepository runtimeDataRepository)
  {
    this.runtimeDataRepository = runtimeDataRepository;
  }

  @Override
  public SCORMContentRepository getSCORMContentRepository()
  {
    return scormContentRepository;
  }

  @Override
  public SCORMQuizRepository getSCORMQuizRepository()
  {
    return scormQuizRepository;
  }

  @Override
  public SCORMWrapperRepository getSCORMWrapperRepository()
  {
    return scormWrapperRepository;
  }

  @Override
  public RuntimeDataRepository getRuntimeDataRepository()
  {
    return runtimeDataRepository;
  }
}

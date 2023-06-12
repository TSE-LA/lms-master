package mn.erin.lms.base.scorm.repository;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface SCORMRepositoryRegistry
{
  SCORMContentRepository getSCORMContentRepository();

  SCORMQuizRepository getSCORMQuizRepository();

  SCORMWrapperRepository getSCORMWrapperRepository();

  RuntimeDataRepository getRuntimeDataRepository();
}

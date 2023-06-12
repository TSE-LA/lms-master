package mn.erin.lms.base.domain.service;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface CourseLauncher<T>
{
  T launch(String courseId, String courseContentId);
}

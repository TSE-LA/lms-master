package mn.erin.lms.base.domain.repository;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LmsRepositoryException extends Exception
{
  public LmsRepositoryException(String message)
  {
    super(message);
  }

  public LmsRepositoryException(String message, Throwable cause)
  {
    super(message, cause);
  }
}

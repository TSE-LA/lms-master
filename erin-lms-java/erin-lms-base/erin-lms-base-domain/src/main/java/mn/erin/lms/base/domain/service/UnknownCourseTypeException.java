package mn.erin.lms.base.domain.service;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class UnknownCourseTypeException extends Exception
{
  public UnknownCourseTypeException(String message)
  {
    super(message);
  }

  public UnknownCourseTypeException(String message, Throwable cause)
  {
    super(message, cause);
  }
}

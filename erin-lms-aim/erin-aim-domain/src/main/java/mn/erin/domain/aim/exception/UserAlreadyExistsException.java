package mn.erin.domain.aim.exception;

/**
 * @author Munkh
 */
public class UserAlreadyExistsException extends Exception
{
  public UserAlreadyExistsException(String message)
  {
    super(message);
  }

  public UserAlreadyExistsException(String message, Throwable cause)
  {
    super(message, cause);
  }
}

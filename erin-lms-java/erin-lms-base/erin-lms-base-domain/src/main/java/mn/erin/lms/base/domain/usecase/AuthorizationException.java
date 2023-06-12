package mn.erin.lms.base.domain.usecase;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class AuthorizationException extends RuntimeException
{
  public AuthorizationException(String message)
  {
    super(message);
  }

  public AuthorizationException(String message, Throwable cause)
  {
    super(message, cause);
  }
}

package mn.erin.lms.base.analytics.exception;

/**
 * @author Munkh
 */
public class AnalyticsRepositoryException extends Exception
{
  public AnalyticsRepositoryException(String message)
  {
    super(message);
  }

  public AnalyticsRepositoryException(String message, Throwable cause)
  {
    super(message, cause);
  }
}

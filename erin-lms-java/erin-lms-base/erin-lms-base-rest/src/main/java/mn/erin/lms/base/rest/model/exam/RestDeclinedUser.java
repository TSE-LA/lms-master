package mn.erin.lms.base.rest.model.exam;

import java.util.Set;

/**
 * @author Galsan Bayart.
 */
public class RestDeclinedUser
{
  String userId;
  String reason;

  public RestDeclinedUser()
  {
    //doNothing
  }

  public String getUserId()
  {
    return userId;
  }

  public void setUserId(String userId)
  {
    this.userId = userId;
  }

  public String getReason()
  {
    return reason;
  }

  public void setReason(String reason)
  {
    this.reason = reason;
  }
}

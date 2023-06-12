package mn.erin.lms.base.domain.model.exam;

/**
 * @author Galsan Bayart
 */
public class DeclinedUserInfo
{
  String userId;
  String reason;

  public DeclinedUserInfo(String userId, String reason)
  {
    this.userId = userId;
    this.reason = reason;
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

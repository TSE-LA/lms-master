package mn.erin.lms.base.mongo.document.exam;

/**
 * @author Galsan Bayart.
 */
public class MongoDeclinedUserInfo
{
  private String user;
  private String reason;

  public MongoDeclinedUserInfo(String user, String reason)
  {
    this.user = user;
    this.reason = reason;
  }

  public String getUser()
  {
    return user;
  }

  public void setUser(String user)
  {
    this.user = user;
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

package mn.erin.lms.base.rest.model;

/**
 * @author Erdenetulga
 */
public class RestCourseState
{
  private String state;
  private String reason;
  private boolean rollback;

  public String getState()
  {
    return state;
  }

  public void setState(String state)
  {
    this.state = state;
  }

  public boolean isRollback()
  {
    return rollback;
  }

  public void setRollback(boolean rollback)
  {
    this.rollback = rollback;
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

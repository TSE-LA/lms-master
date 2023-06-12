package mn.erin.lms.base.rest.model;

/**
 * @author Munkh
 */
public class RestCategoryAutoEnroll
{
  private boolean autoEnroll;

  public RestCategoryAutoEnroll()
  {
  }

  public RestCategoryAutoEnroll(boolean autoEnroll)
  {
    this.autoEnroll = autoEnroll;
  }

  public boolean isAutoEnroll()
  {
    return autoEnroll;
  }

  public void setAutoEnroll(boolean autoEnroll)
  {
    this.autoEnroll = autoEnroll;
  }
}

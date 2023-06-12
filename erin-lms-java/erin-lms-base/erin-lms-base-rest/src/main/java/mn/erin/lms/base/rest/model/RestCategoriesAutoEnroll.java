package mn.erin.lms.base.rest.model;

import java.util.Set;

/**
 * @author Munkh
 */
public class RestCategoriesAutoEnroll
{
  private Set<String> ids;
  private boolean autoEnroll;

  public RestCategoriesAutoEnroll()
  {
  }

  public RestCategoriesAutoEnroll(Set<String> ids, boolean autoEnroll)
  {
    this.ids = ids;
    this.autoEnroll = autoEnroll;
  }

  public Set<String> getIds()
  {
    return ids;
  }

  public void setIds(Set<String> ids)
  {
    this.ids = ids;
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

package mn.erin.lms.base.rest.api.aim.model;

/**
 * @author Munkh
 */
public class RestUsersArchiveBody
{
  private String[] userIds;
  private boolean archived;

  public String[] getUserIds()
  {
    return userIds;
  }

  public boolean isArchived()
  {
    return archived;
  }

  public void setArchived(boolean archived)
  {
    this.archived = archived;
  }
}

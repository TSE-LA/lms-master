package mn.erin.lms.base.rest.api.aim.model;

/**
 * @author Munkh
 */
public class RestUserArchiveBody
{
  private boolean archived;

  public boolean isArchived()
  {
    return archived;
  }

  public void setArchived(boolean archived)
  {
    this.archived = archived;
  }
}

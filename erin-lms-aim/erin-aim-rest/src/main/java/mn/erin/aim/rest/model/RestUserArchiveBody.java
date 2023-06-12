package mn.erin.aim.rest.model;

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

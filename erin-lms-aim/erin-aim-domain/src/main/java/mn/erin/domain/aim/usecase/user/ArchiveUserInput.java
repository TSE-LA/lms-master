package mn.erin.domain.aim.usecase.user;

/**
 * @author Munkh
 */
public class ArchiveUserInput
{
  private final String userId;
  private boolean archived;

  public ArchiveUserInput(String userId, boolean archived)
  {
    this.userId = userId;
    this.archived = archived;
  }

  public String getUserId()
  {
    return userId;
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

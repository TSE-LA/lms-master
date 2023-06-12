package mn.erin.domain.aim.usecase.membership;

/**
 * @author Zorig
 */
public class ChangeUserGroupOutput
{
  private final boolean isUpdated;

  public ChangeUserGroupOutput(boolean isUpdated)
  {
    this.isUpdated = isUpdated;
  }

  public boolean isUpdated()
  {
    return isUpdated;
  }
}

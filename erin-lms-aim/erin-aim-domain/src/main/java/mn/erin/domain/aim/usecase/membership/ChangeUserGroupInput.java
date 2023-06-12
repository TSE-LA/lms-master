package mn.erin.domain.aim.usecase.membership;

/**
 * @author Zorig
 */
public class ChangeUserGroupInput
{
  // TODO this model is lacking parameter for previous group ID
  private final String username;
  private final String newGroupId;

  public ChangeUserGroupInput(String username, String newGroupId)
  {
    this.username = username;
    this.newGroupId = newGroupId;
  }

  public String getUsername()
  {
    return username;
  }

  public String getNewGroupId()
  {
    return newGroupId;
  }
}

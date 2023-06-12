package mn.erin.domain.aim.usecase.membership;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetUserMembershipsInput
{
  private final String username;

  public GetUserMembershipsInput(String username)
  {
    this.username = Validate.notBlank(username, "Username cannot be null or blank");
  }

  public String getUsername()
  {
    return username;
  }
}

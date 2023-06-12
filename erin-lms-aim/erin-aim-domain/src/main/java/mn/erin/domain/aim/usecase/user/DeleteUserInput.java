package mn.erin.domain.aim.usecase.user;

import org.apache.commons.lang3.Validate;

/**
 * @author Munkh
 */
public class DeleteUserInput
{
  private final String userId;

  public DeleteUserInput(String userId)
  {
    this.userId = Validate.notBlank(userId, "User ID required!");
  }

  public String getUserId()
  {
    return userId;
  }
}

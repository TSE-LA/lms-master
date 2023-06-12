package mn.erin.domain.aim.usecase.user;

import mn.erin.domain.aim.model.user.UserAggregate;

/**
 * @author Munkh
 */
public class CreateUserOutput
{
  private final UserAggregate userAggregate;

  public CreateUserOutput(UserAggregate userAggregate)
  {
    this.userAggregate = userAggregate;
  }

  public UserAggregate getUserAggregate()
  {
    return userAggregate;
  }
}

package mn.erin.lms.base.domain.usecase.user.dto;

import java.util.List;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class GetUserByRolesInput
{
  private final List<String> roles;
  private final boolean includeMe;

  public GetUserByRolesInput(List<String> roles, boolean includeMe)
  {
    this.roles = roles;
    this.includeMe = includeMe;
  }

  public List<String> getRoles()
  {
    return roles;
  }

  public boolean isCurrentUserIncluded()
  {
    return includeMe;
  }
}

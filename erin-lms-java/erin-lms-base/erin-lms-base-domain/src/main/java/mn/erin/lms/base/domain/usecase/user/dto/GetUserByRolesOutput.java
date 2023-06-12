package mn.erin.lms.base.domain.usecase.user.dto;

import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.user.UserAggregate;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class GetUserByRolesOutput
{
  private final UserAggregate user;
  private Membership membership;
  private String groupPath;

  public GetUserByRolesOutput(UserAggregate user)
  {
    this.user = user;
  }

  public UserAggregate getUser()
  {
    return user;
  }

  public Membership getMembership()
  {
    return membership;
  }

  public void setMembership(Membership membership)
  {
    this.membership = membership;
  }

  public String getGroupPath()
  {
    return groupPath;
  }

  public void setGroupPath(String groupPath)
  {
    this.groupPath = groupPath;
  }
}

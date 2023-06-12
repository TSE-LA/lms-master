package mn.erin.lms.base.rest.model.exam;

import java.util.List;
import java.util.Set;

/**
 * @author Galsan Bayart.
 */
public class RestSupervisorResult
{
  Set<String> suggestedUsers;
  List<RestDeclinedUser> declinedUsers;

  public RestSupervisorResult()
  {
    //doNothing
  }

  public Set<String> getSuggestedUsers()
  {
    return suggestedUsers;
  }

  public void setSuggestedUsers(Set<String> suggestedUsers)
  {
    this.suggestedUsers = suggestedUsers;
  }

  public List<RestDeclinedUser> getDeclinedUsers()
  {
    return declinedUsers;
  }

  public void setDeclinedUsers(List<RestDeclinedUser> declinedUsers)
  {
    this.declinedUsers = declinedUsers;
  }
}

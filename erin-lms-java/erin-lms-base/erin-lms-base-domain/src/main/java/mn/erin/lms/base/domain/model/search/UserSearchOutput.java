package mn.erin.lms.base.domain.model.search;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;

/**
 * @author Galsan Bayart.
 */
public class UserSearchOutput
{
  private Set<String> usersWithoutGroup;
  private List<UsersWithGroup> usersWithGroup;

  public UserSearchOutput(Set<String> usersWithoutGroup, List<UsersWithGroup> usersWithGroup)
  {
    this.usersWithoutGroup = Validate.notNull(usersWithoutGroup);
    this.usersWithGroup = Validate.notNull(usersWithGroup);
  }

  public Set<String> getUsersWithoutGroup()
  {
    return usersWithoutGroup;
  }

  public void setUsersWithoutGroup(Set<String> usersWithoutGroup)
  {
    this.usersWithoutGroup = usersWithoutGroup;
  }

  public List<UsersWithGroup> getUsersWithGroup()
  {
    return usersWithGroup;
  }

  public void setUsersWithGroup(List<UsersWithGroup> usersWithGroup)
  {
    this.usersWithGroup = usersWithGroup;
  }
}

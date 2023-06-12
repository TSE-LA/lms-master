package mn.erin.domain.aim.model.user;

import java.util.List;

public class UserDuplicity
{
  private final List<String> duplicatedUsers;
  private final Integer registeredUserCount;
  private final List<String> failedUsers;

  public UserDuplicity(List<String> duplicatedUsers, Integer registeredUserCount, List<String> failedUsers)
  {
    this.duplicatedUsers = duplicatedUsers;
    this.registeredUserCount = registeredUserCount;
    this.failedUsers = failedUsers;
  }
    public List<String> getDuplicatedUsers() { return duplicatedUsers; }
    public Integer getRegisteredUserCount() { return registeredUserCount;}
    public List<String> getFailedToRegisterUsers() { return failedUsers;}

}

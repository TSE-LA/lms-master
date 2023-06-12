package mn.erin.lms.base.domain.model.search;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.ValueObject;

/**
 * @author Galsan Bayart.
 */
public class UsersWithGroup implements ValueObject<UsersWithGroup>
{
  private final String userName;
  private final String groupPath;
  private final int groupDepth;

  public UsersWithGroup(String userName, String groupPath, int groupDepth)
  {
    this.userName = Validate.notBlank(userName);
    this.groupPath = Validate.notBlank(groupPath);
    this.groupDepth = groupDepth;
  }

  public String getUserName()
  {
    return userName;
  }

  public String getGroupPath()
  {
    return groupPath;
  }

  public int getGroupDepth()
  {
    return groupDepth;
  }

  @Override
  public boolean sameValueAs(UsersWithGroup other)
  {
    return this.equals(other);
  }
}

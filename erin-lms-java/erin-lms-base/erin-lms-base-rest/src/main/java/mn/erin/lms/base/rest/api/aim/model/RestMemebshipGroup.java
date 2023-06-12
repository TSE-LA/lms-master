package mn.erin.lms.base.rest.api.aim.model;

public class RestMemebshipGroup
{
  private final String groupId;
  private final String groupName;

  public RestMemebshipGroup(String groupId, String groupName)
  {
    this.groupId = groupId;
    this.groupName = groupName;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public String getGroupName()
  {
    return groupName;
  }
}

package mn.erin.lms.legacy.infrastructure.unitel.rest.promotion;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class PromoReadershipRequest
{
  private String groupId;
  private String newGroupId;
  private String learnerId;

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String groupId)
  {
    this.groupId = groupId;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public void setLearnerId(String learnerId)
  {
    this.learnerId = learnerId;
  }

  public String getNewGroupId()
  {
    return newGroupId;
  }

  public void setNewGroupId(String newGroupId)
  {
    this.newGroupId = newGroupId;
  }
}

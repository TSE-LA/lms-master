package mn.erin.lms.legacy.infrastructure.unitel.rest.promotion;

public class MovePromoReadershipsRequest
{
  private String groupId;
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
}

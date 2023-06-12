package mn.erin.lms.legacy.domain.lms.usecase.readership.create_readership;

/**
 * @author Munkh
 */
public class GetNotEnrolledCoursesInput
{
  private String learnerId;
  private String groupId;
  private String newGroupId;

  public GetNotEnrolledCoursesInput(String learnerId, String groupId)
  {
    this.learnerId = learnerId;
    this.groupId = groupId;
  }

  public GetNotEnrolledCoursesInput(String learnerId, String groupId, String newGroupId)
  {
    this.learnerId = learnerId;
    this.groupId = groupId;
    this.newGroupId = newGroupId;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String groupId)
  {
    this.groupId = groupId;
  }

  public String getNewGroupId()
  {
    return newGroupId;
  }

  public void setNewGroupId(String newGroupId)
  {
    this.newGroupId = newGroupId;
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

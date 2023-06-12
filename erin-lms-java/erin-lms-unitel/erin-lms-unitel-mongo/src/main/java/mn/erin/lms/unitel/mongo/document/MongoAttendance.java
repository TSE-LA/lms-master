package mn.erin.lms.unitel.mongo.document;

import java.util.List;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class MongoAttendance
{
  private String learnerId;
  private String groupName;
  private boolean isPresent;
  private List<Integer> scores;
  private String supervisorId;

  public MongoAttendance()
  {
  }

  public MongoAttendance(String learnerId, boolean isPresent, List<Integer> scores)
  {
    this.learnerId = learnerId;
    this.isPresent = isPresent;
    this.scores = scores;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public void setLearnerId(String learnerId)
  {
    this.learnerId = learnerId;
  }

  public String getGroupName()
  {
    return groupName;
  }

  public void setGroupName(String groupName)
  {
    this.groupName = groupName;
  }

  public boolean isPresent()
  {
    return isPresent;
  }

  public void setPresent(boolean present)
  {
    isPresent = present;
  }

  public List<Integer> getScores()
  {
    return scores;
  }

  public void setScores(List<Integer> scores)
  {
    this.scores = scores;
  }

  public String getSupervisorId()
  {
    return supervisorId;
  }

  public void setSupervisorId(String supervisorId)
  {
    this.supervisorId = supervisorId;
  }
}

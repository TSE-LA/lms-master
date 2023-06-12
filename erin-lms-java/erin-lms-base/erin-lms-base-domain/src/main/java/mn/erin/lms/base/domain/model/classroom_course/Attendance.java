package mn.erin.lms.base.domain.model.classroom_course;

import java.util.List;

import mn.erin.lms.base.aim.user.LearnerId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class Attendance
{
  private final LearnerId learnerId;
  private String groupName;
  private boolean isInvited;
  private final boolean isPresent;
  private final List<Integer> scores;
  private LearnerId supervisorId;

  public Attendance(LearnerId learnerId, boolean isPresent, List<Integer> scores)
  {
    this.learnerId = learnerId;
    this.isPresent = isPresent;
    this.scores = scores;
  }

  public LearnerId getLearnerId()
  {
    return learnerId;
  }

  public boolean isPresent()
  {
    return isPresent;
  }

  public List<Integer> getScores()
  {
    return scores;
  }

  public String getGroupName()
  {
    return groupName;
  }

  public LearnerId getSupervisorId()
  {
    return supervisorId;
  }

  public void setSupervisorId(LearnerId supervisorId)
  {
    this.supervisorId = supervisorId;
  }

  public void setGroupName(String groupName)
  {
    this.groupName = groupName;
  }

  public boolean isInvited()
  {
    return isInvited;
  }

  public void setInvited(boolean invited)
  {
    isInvited = invited;
  }
}

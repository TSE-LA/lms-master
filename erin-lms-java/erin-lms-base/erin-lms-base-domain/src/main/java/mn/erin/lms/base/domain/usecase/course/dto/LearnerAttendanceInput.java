package mn.erin.lms.base.domain.usecase.course.dto;

import java.util.List;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LearnerAttendanceInput
{
  private final String learnerId;
  private final boolean isPresent;
  private final List<Integer> grades;
  private boolean isInvited;
  private String groupName;
  private String supervisorId;

  public LearnerAttendanceInput(String learnerId, boolean isPresent, List<Integer> grades)
  {
    this.learnerId = learnerId;
    this.isPresent = isPresent;
    this.grades = grades;
  }
  public LearnerAttendanceInput(String learnerId, boolean isPresent, List<Integer> grades, String groupName, String supervisorId, boolean isInvited)
  {
    this.learnerId = learnerId;
    this.isPresent = isPresent;
    this.isInvited = isInvited;
    this.grades = grades;
    this.groupName = groupName;
    this.supervisorId = supervisorId;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public boolean isPresent()
  {
    return isPresent;
  }

  public List<Integer> getGrades()
  {
    return grades;
  }

  public String getGroupName()
  {
    return groupName;
  }

  public void setGroupName(String groupName)
  {
    this.groupName = groupName;
  }

  public String getSupervisorId()
  {
    return supervisorId;
  }

  public void setSupervisorId(String supervisorId)
  {
    this.supervisorId = supervisorId;
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

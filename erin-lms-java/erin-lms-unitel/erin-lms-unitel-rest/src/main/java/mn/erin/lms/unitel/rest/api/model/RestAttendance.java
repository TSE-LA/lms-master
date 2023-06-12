package mn.erin.lms.unitel.rest.api.model;

import java.util.List;

/**
 * @author Munkh
 */
public class RestAttendance
{
  private String learnerId;
  private String groupName;
  private boolean isPresent;
  private List<Integer> grades;
  private String supervisorId;

  public RestAttendance()
  {
  }

  public RestAttendance(String learnerId, boolean isPresent, List<Integer> grades)
  {
    this.learnerId = learnerId;
    this.isPresent = isPresent;
    this.grades = grades;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public void setLearnerId(String learnerId)
  {
    this.learnerId = learnerId;
  }

  public boolean isPresent()
  {
    return isPresent;
  }

  public void setPresent(boolean present)
  {
    isPresent = present;
  }

  public List<Integer> getGrades()
  {
    return grades;
  }

  public void setGrades(List<Integer> grades)
  {
    this.grades = grades;
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
}

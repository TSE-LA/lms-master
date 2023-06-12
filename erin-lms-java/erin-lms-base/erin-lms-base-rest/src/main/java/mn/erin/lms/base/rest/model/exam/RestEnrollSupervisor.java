package mn.erin.lms.base.rest.model.exam;

import java.util.Set;

/**
 * @author Galsan Bayart.
 */
public class RestEnrollSupervisor
{
  String examId;
  Set<String> supervisorIds;
  Set<String> adminAddedUser;

  public RestEnrollSupervisor()
  {
  }

  public String getExamId()
  {
    return examId;
  }

  public void setExamId(String examId)
  {
    this.examId = examId;
  }

  public Set<String> getSupervisorIds()
  {
    return supervisorIds;
  }

  public void setSupervisorIds(Set<String> supervisorIds)
  {
    this.supervisorIds = supervisorIds;
  }

  public Set<String> getAdminAddedUser()
  {
    return adminAddedUser;
  }

  public void setAdminAddedUser(Set<String> adminAddedUser)
  {
    this.adminAddedUser = adminAddedUser;
  }
}

package mn.erin.lms.base.domain.model.enrollment;

import java.util.Set;

/**
 * @author Galsan Bayart.
 */
public class EnrollSupervisor
{
  private String examId;
  private Set<String> supervisorIds;
  private Set<String> adminAddedUser;

  public EnrollSupervisor(String examId, Set<String> supervisorIds, Set<String> adminAddedUser)
  {
    this.examId = examId;
    this.supervisorIds = supervisorIds;
    this.adminAddedUser = adminAddedUser;
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



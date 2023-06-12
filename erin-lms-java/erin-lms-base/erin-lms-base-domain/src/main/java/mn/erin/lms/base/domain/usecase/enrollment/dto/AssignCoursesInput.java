package mn.erin.lms.base.domain.usecase.enrollment.dto;

import java.util.List;

/**
 *
 * @author Munkh
 */
public class AssignCoursesInput
{
  private final List<String> userIds;
  private final String roleId;
  private final String departmentId;

  public AssignCoursesInput(List<String> userIds, String roleId, String departmentId)
  {
    this.userIds = userIds;
    this.roleId = roleId;
    this.departmentId = departmentId;
  }

  public List<String> getUserIds()
  {
    return userIds;
  }

  public String getRoleId()
  {
    return roleId;
  }

  public String getDepartmentId()
  {
    return departmentId;
  }
}

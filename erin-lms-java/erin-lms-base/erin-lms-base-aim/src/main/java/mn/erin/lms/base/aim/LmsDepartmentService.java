package mn.erin.lms.base.aim;

import java.util.Set;
/**
 * @author Bat-Erdene Tsogoo.
 */
public interface LmsDepartmentService
{
  String getCurrentDepartmentId();

  String getRole(String userId);

  String getDepartmentId(String departmentName);

  String getDepartmentName(String departmentId);

  Set<String> getSubDepartments(String departmentId);

  Set<String> getParentDepartments(String departmentId);

  Set<String> getInstructors(String departmentId);

  Set<String> getLearners(String departmentId);

  Set<String> getAllLearners(String departmentId);

  Set<String> getLearnersByRole(String departmentId, String role);

  Set<String> getParentLearnersByRole(String departmentId, String role);
}

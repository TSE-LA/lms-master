package mn.erin.lms.jarvis.domain.report.usecase;

import java.util.HashSet;
import java.util.Set;

import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;

public class GetAllUsersByGroup
{
  private final AccessIdentityManagement accessIdentityManagement;
  private final LmsDepartmentService departmentService;

  public GetAllUsersByGroup(AccessIdentityManagement accessIdentityManagement, LmsServiceRegistry jarvisLmsServiceRegistry)
  {
    this.accessIdentityManagement = accessIdentityManagement;
    this.departmentService = jarvisLmsServiceRegistry.getDepartmentService();
  }
  public Set<String> execute(String groupId) {
    Set<String> allGroups = new HashSet<>();
    Set<String> learners = new HashSet<>();
    allGroups.add(groupId);
    allGroups.addAll(departmentService.getSubDepartments(groupId));
    for (String group : allGroups)
    {
      learners.addAll(accessIdentityManagement.getLearners(group));
    }
    return learners;
  }
}

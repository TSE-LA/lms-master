package mn.erin.lms.base.domain.usecase.user;

import java.util.HashSet;
import java.util.Set;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.base.aim.LmsDepartmentService;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetInstructors implements UseCase<Void, Set<String>>
{
  private final LmsDepartmentService departmentService;

  public GetInstructors(LmsDepartmentService departmentService)
  {
    this.departmentService = departmentService;
  }

  @Override
  public Set<String> execute(Void input)
  {
    Set<String> allDepartments = new HashSet<>();
    String departmentId = departmentService.getCurrentDepartmentId();
    for(String subDepartment : departmentService.getSubDepartments(departmentId)){
        allDepartments.addAll(departmentService.getInstructors(subDepartment));
    }
    return allDepartments;
  }
}

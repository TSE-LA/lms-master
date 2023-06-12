package mn.erin.lms.unitel.domain.usecase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.aim.LmsUserService;
import mn.erin.lms.unitel.domain.model.analytics.EmployeePromoPoints;
import mn.erin.lms.unitel.domain.model.user.Membership;
import mn.erin.lms.unitel.domain.service.EmployeeAnalyticsService;
import mn.erin.lms.unitel.domain.usecase.dto.EmployeePromoPointsDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetEmployeePromoPoints implements UseCase<Void, EmployeePromoPointsDto>
{
  private final AccessIdentityManagement aimService;
  private final LmsUserService userService;
  private final LmsDepartmentService departmentService;
  private final EmployeeAnalyticsService employeeAnalyticsService;

  public GetEmployeePromoPoints(AccessIdentityManagement aimService, EmployeeAnalyticsService employeeAnalyticsService,
      LmsServiceRegistry lmsServiceRegistry)
  {
    this.aimService = aimService;
    this.employeeAnalyticsService = employeeAnalyticsService;
    this.userService = lmsServiceRegistry.getLmsUserService();
    this.departmentService = lmsServiceRegistry.getDepartmentService();
  }

  @Override
  public EmployeePromoPointsDto execute(Void input)
  {
    LmsUser user = userService.getCurrentUser();
    String currentUserDepartment = departmentService.getCurrentDepartmentId();

    Set<String> employees = departmentService.getLearners(currentUserDepartment);
    List<Membership> memberships = new ArrayList<>();

    for (String employee : employees)
    {
      memberships.add(new Membership(employee, aimService.getRole(employee)));
    }

    List<EmployeePromoPoints> employeePromoPoints = employeeAnalyticsService.getPromotionPoints(memberships);

    EmployeePromoPointsDto dto = new EmployeePromoPointsDto();

    for (EmployeePromoPoints employeePromoPoint : employeePromoPoints)
    {
      if (user.getId().getId().equalsIgnoreCase(employeePromoPoint.getUserName()))
      {
        dto.setCurrentEmployeeScore(employeePromoPoint.getPromoPoints());
      }
      else
      {
        dto.addOtherEmployeeScore(employeePromoPoint.getPromoPoints());
      }
    }

    return dto;
  }
}

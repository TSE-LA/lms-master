package mn.erin.lms.legacy.domain.unitel.usecase.analytics.employee_analytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.membership.MembershipId;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.legacy.domain.unitel.model.EmployeePromotionPoints;
import mn.erin.lms.legacy.domain.unitel.service.EmployeeAnalytics;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetEmployeePromoPoints implements UseCase<Void, EmployeePromoPointsOutput>
{
  private final AccessIdentityManagement accessIdentityManagement;
  private final EmployeeAnalytics employeeAnalytics;

  public GetEmployeePromoPoints(AccessIdentityManagement accessIdentityManagement, EmployeeAnalytics employeeAnalytics)
  {
    this.accessIdentityManagement = Validate.notNull(accessIdentityManagement);
    this.employeeAnalytics = Validate.notNull(employeeAnalytics, "EmployeeAnalytics cannot be null!");
  }

  @Override
  public EmployeePromoPointsOutput execute(Void input)
  {
    String currentUsername = accessIdentityManagement.getCurrentUsername();
    String currentUserGroup = accessIdentityManagement.getUserDepartmentId(currentUsername);

    Set<String> users = accessIdentityManagement.getLearners(currentUserGroup);
    List<Membership> employees = new ArrayList<>();

    for (String user : users)
    {
      String role = accessIdentityManagement.getRole(user);
      employees.add(new Membership(MembershipId.valueOf(user), user, GroupId.valueOf(currentUserGroup), RoleId.valueOf(role)));
    }

    List<EmployeePromotionPoints> employeePromotionPoints = employeeAnalytics.getPromotionPoints(employees);

    EmployeePromoPointsOutput output = new EmployeePromoPointsOutput();

    for (EmployeePromotionPoints employeePromotionPoint : employeePromotionPoints)
    {
      if (currentUsername.equalsIgnoreCase(employeePromotionPoint.getUserName()))
      {
        output.setCurrentEmployeeScore(employeePromotionPoint.getPromoPoints());
      }
      else
      {
        output.addOtherEmployeeScore(employeePromotionPoint.getPromoPoints());
      }
    }

    return output;
  }
}

package mn.erin.lms.legacy.domain.unitel.usecase.analytics.employee_analytics;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.legacy.domain.unitel.model.EmployeePromotionPoints;
import mn.erin.lms.legacy.domain.unitel.service.EmployeeAnalytics;

/**
 * @author Munkh
 */
public class GetEmployeeTotalScore implements UseCase<String, Integer>
{
  private final EmployeeAnalytics employeeAnalytics;

  public GetEmployeeTotalScore(EmployeeAnalytics employeeAnalytics)
  {
    this.employeeAnalytics = Validate.notNull(employeeAnalytics, "EmployeeAnalytics cannot be null!");
  }

  @Override
  public Integer execute(String userId)
  {
    EmployeePromotionPoints employeePromotionPoint = employeeAnalytics.getPromotionPoint(userId);
    return employeePromotionPoint != null ? employeePromotionPoint.getPromoPoints() : 0;
  }
}

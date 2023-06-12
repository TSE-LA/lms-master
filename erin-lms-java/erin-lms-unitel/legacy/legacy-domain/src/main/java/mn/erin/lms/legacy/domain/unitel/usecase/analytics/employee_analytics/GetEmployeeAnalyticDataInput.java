package mn.erin.lms.legacy.domain.unitel.usecase.analytics.employee_analytics;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetEmployeeAnalyticDataInput
{
  private String employeeId;

  public GetEmployeeAnalyticDataInput(String employeeId)
  {
    this.employeeId = Validate.notBlank(employeeId, "Employee ID cannot be null or blank!");
  }

  public String getEmployeeId()
  {
    return employeeId;
  }
}

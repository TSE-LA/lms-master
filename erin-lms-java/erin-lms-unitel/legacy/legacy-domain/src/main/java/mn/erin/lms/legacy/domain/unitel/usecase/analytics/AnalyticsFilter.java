package mn.erin.lms.legacy.domain.unitel.usecase.analytics;

import java.util.Date;

import org.apache.commons.lang3.Validate;

import mn.erin.lms.legacy.domain.unitel.usecase.DateFilter;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class AnalyticsFilter extends DateFilter
{
  private String groupId;
  private String userId;

  public AnalyticsFilter(Date startDate, Date endDate)
  {
    super(startDate, endDate);
  }

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String groupId)
  {
    this.groupId = groupId;
  }

  public String getUserId()
  {
    return userId;
  }

  public void setUserId(String userId)
  {
    this.userId = Validate.notBlank(userId, "User id cannot be blank!");
  }
}

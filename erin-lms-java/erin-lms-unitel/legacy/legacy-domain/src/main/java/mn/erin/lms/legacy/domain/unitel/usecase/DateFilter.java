package mn.erin.lms.legacy.domain.unitel.usecase;

import java.util.Date;

import org.apache.commons.lang3.Validate;

import mn.erin.common.datetime.DateTimeUtils;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class DateFilter
{
  private final Date startDate;
  private final Date endDate;

  public DateFilter(Date startDate, Date endDate)
  {
    this.startDate = Validate.notNull(startDate, "Start date cannot be null!");
    this.endDate = Validate.notNull(endDate, "End date cannot be null!");

    if (DateTimeUtils.compare(startDate, endDate) > 0)
    {
      throw new IllegalArgumentException("Start date cannot be greater than end date");
    }
  }

  public Date getStartDate()
  {
    return startDate;
  }

  public Date getEndDate()
  {
    return endDate;
  }
}

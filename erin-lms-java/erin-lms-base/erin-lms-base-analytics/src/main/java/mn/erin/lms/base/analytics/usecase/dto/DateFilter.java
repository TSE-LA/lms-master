package mn.erin.lms.base.analytics.usecase.dto;

import java.time.LocalDate;

/**
 * @author Munkh
 */
public class DateFilter
{
  private final LocalDate startDate;
  private final LocalDate endDate;

  public DateFilter(LocalDate startDate, LocalDate endDate)
  {
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public LocalDate getStartDate()
  {
    return startDate;
  }

  public LocalDate getEndDate()
  {
    return endDate;
  }
}

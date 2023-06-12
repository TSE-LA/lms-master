package mn.erin.lms.unitel.domain.usecase.dto;

import java.time.LocalDate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class DateFilter
{
  private final LocalDate startDate;
  private final LocalDate endDate;

  public DateFilter(LocalDate startDate, LocalDate endDate)
  {
    this.startDate = startDate;
    this.endDate = endDate;

    if (startDate.isAfter(endDate))
    {
      throw new IllegalArgumentException("Start date cannot be greater than end date");
    }
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

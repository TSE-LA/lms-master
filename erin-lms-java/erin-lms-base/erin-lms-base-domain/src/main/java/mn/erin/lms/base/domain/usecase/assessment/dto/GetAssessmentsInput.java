package mn.erin.lms.base.domain.usecase.assessment.dto;

import java.time.LocalDate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetAssessmentsInput
{
  private LocalDate startDate;
  private LocalDate endDate;
  private Boolean onlyActive;

  public GetAssessmentsInput(LocalDate startDate, LocalDate endDate, Boolean onlyActive)
  {
    this.startDate = startDate;
    this.endDate = endDate;
    this.onlyActive = onlyActive;
  }

  public LocalDate getStartDate()
  {
    return startDate;
  }

  public LocalDate getEndDate()
  {
    return endDate;
  }

  public Boolean getOnlyActive()
  {
    return onlyActive;
  }

  public void setOnlyActive(Boolean onlyActive)
  {
    this.onlyActive = onlyActive;
  }
}

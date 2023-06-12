package mn.erin.lms.base.domain.usecase.announcement.dto;

public class GetAllAnnouncementsInput
{
  private String startDate;
  private String endDate;

  public GetAllAnnouncementsInput(String startDate, String endDate)
  {
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public String getStartDate()
  {
    return startDate;
  }

  public String getEndDate()
  {
    return endDate;
  }
}

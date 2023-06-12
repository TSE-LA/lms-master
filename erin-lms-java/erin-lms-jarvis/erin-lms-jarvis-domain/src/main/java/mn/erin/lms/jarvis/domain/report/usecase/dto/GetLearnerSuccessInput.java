package mn.erin.lms.jarvis.domain.report.usecase.dto;

/**
 * @author Galsan Bayart
 */
public class GetLearnerSuccessInput
{
  private String learnerId;
  private int year;
  private DateType dateType;
  private GroupType groupType;

  public GetLearnerSuccessInput(String learnerId, int year, DateType dateType, GroupType groupType)
  {
    this.learnerId = learnerId;
    this.year = year;
    this.dateType = dateType;
    this.groupType = groupType;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public void setLearnerId(String learnerId)
  {
    this.learnerId = learnerId;
  }

  public int getYear()
  {
    return year;
  }

  public void setYear(int year)
  {
    this.year = year;
  }

  public DateType getDateType()
  {
    return dateType;
  }

  public void setDateType(DateType dateType)
  {
    this.dateType = dateType;
  }

  public GroupType getGroupType()
  {
    return groupType;
  }

  public void setGroupType(GroupType groupType)
  {
    this.groupType = groupType;
  }
}

package mn.erin.lms.unitel.domain.usecase.dto;

/**
 * @author Byambajav
 */
public class LearnerSuccessInput
{
  private String learnerId;
  private String courseType;
  private int year;
  private DateType dateType;
  private GroupType groupType;

  public LearnerSuccessInput(String learnerId, String courseType, int year, DateType dateType, GroupType groupType)
  {
    this.learnerId = learnerId;
    this.courseType = courseType;
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

  public String getCourseType()
  {
    return courseType;
  }

  public void setCourseType(String courseType)
  {
    this.courseType = courseType;
  }
}

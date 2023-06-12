package mn.erin.lms.legacy.domain.lms.usecase.audit.dto;

import java.util.Date;

/**
 * @author Galsan Bayart
 */
public class EnrollmentToReaderShipInput
{
  private Date startDate;
  private Date endDate;
  private String groupId;
  private String state;

  public EnrollmentToReaderShipInput() {
  }

  public EnrollmentToReaderShipInput(Date startDate, Date endDate, String groupId, String state)
  {
    this.startDate = startDate;
    this.endDate = endDate;
    this.groupId = groupId;
    this.state = state;
  }

  public Date getStartDate()
  {
    return startDate;
  }

  public void setStartDate(Date startDate)
  {
    this.startDate = startDate;
  }

  public Date getEndDate()
  {
    return endDate;
  }

  public String getState() { return state;}

  public void setEndDate(Date endDate)
  {
    this.endDate = endDate;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String groupId)
  {
    this.groupId = groupId;
  }
}

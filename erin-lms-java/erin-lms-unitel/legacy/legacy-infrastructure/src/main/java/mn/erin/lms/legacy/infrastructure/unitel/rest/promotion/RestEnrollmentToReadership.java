package mn.erin.lms.legacy.infrastructure.unitel.rest.promotion;

import java.util.Date;

/**
 * @author Galsan Bayart
 */
public class RestEnrollmentToReadership
{
  private Date startDate;
  private Date endDate;
  private String state;

  public RestEnrollmentToReadership(Date startDate, Date endDate)
  {
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public RestEnrollmentToReadership()
  {
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

  public void setEndDate(Date endDate)
  {
    this.endDate = endDate;
  }

  public String getState() { return state;}

  public void setState( String state) { this.state = state ;}
}

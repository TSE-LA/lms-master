package mn.erin.lms.base.domain.usecase.course.dto;

/**
 * @author Temuulen Naranbold
 */
public class LearnerCreditDto
{
  private double totalCredit;
  private double yearCredit;

  public LearnerCreditDto(double totalCredit, double yearCredit)
  {
    this.totalCredit = totalCredit;
    this.yearCredit = yearCredit;
  }

  public double getTotalCredit()
  {
    return totalCredit;
  }

  public void setTotalCredit(double totalCredit)
  {
    this.totalCredit = totalCredit;
  }

  public double getYearCredit()
  {
    return yearCredit;
  }

  public void setYearCredit(double yearCredit)
  {
    this.yearCredit = yearCredit;
  }
}

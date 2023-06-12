package mn.erin.lms.base.domain.usecase.exam.dto;

/**
 * @author Byambajav
 */
public class LearnerReportByCategory
{
  private String categoryName;
  private double score;

  public LearnerReportByCategory(String categoryName, double score)
  {
    this.categoryName = categoryName;
    this.score = score;
  }

  public String getCategoryName()
  {
    return categoryName;
  }

  public void setCategoryName(String categoryName)
  {
    this.categoryName = categoryName;
  }

  public double getScore()
  {
    return score;
  }

  public void setScore(double score)
  {
    this.score = score;
  }
}

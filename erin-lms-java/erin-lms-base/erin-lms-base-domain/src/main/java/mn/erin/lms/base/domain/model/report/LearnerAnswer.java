package mn.erin.lms.base.domain.model.report;

/**
 * @author Erdenetulga
 */
public class LearnerAnswer
{
  private String answer;
  private int index;

  public LearnerAnswer()
  {
  }

  public String getAnswer()
  {
    return answer;
  }

  public void setAnswer(String answer)
  {
    this.answer = answer;
  }

  public int getIndex()
  {
    return index;
  }

  public void setIndex(int index)
  {
    this.index = index;
  }
}

package mn.erin.lms.base.analytics.model.survey;

/**
 * @author Munkh
 */
public class SurveyAnswer
{
  private final String value;
  private Integer count;

  public SurveyAnswer(String value)
  {
    this.value = value;
  }

  public SurveyAnswer(String value, Integer count)
  {
    this.value = value;
    this.count = count;
  }

  public String getValue()
  {
    return value;
  }

  public Integer getCount()
  {
    return count;
  }

  public void add(Integer count)
  {
    this.count += count;
  }

  public void increment()
  {
    if (count != null)
    {
      this.count += 1;
    }
  }
}

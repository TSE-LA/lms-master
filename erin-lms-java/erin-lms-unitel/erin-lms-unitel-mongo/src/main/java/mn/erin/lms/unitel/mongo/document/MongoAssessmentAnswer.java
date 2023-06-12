package mn.erin.lms.unitel.mongo.document;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class MongoAssessmentAnswer
{
  private String value;
  private Integer count;

  public MongoAssessmentAnswer()
  {
  }

  public MongoAssessmentAnswer(String value)
  {
    this.value = value;
  }

  public MongoAssessmentAnswer(String value, Integer count)
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
}

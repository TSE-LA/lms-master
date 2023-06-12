package mn.erin.lms.base.rest.api;

/**
 * @author mLkhagvasuren
 */
public class RestCreateExamResult
{
  private final String examId;

  public RestCreateExamResult(String examId)
  {
    this.examId = examId;
  }

  public String getExamId()
  {
    return examId;
  }
}

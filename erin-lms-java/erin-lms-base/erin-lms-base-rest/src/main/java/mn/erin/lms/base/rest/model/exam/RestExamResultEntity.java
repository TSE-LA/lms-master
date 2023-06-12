package mn.erin.lms.base.rest.model.exam;

/**
 * @author Galsan Bayart.
 */
public class RestExamResultEntity
{
  String questionId;
  String answer;

  public RestExamResultEntity()
  {
    //doNothing
  }

  public String getQuestionId()
  {
    return questionId;
  }

  public void setQuestionId(String questionId)
  {
    this.questionId = questionId;
  }

  public String getAnswer()
  {
    return answer;
  }

  public void setAnswer(String answer)
  {
    this.answer = answer;
  }
}

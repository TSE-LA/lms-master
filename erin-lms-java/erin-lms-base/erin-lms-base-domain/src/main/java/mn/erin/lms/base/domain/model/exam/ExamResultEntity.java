package mn.erin.lms.base.domain.model.exam;

/**
 * @author Galsan Bayart.
 */
public class ExamResultEntity
{
  String questionId;
  String answer;

  public ExamResultEntity(String questionId, String answer)
  {
    this.questionId = questionId;
    this.answer = answer;
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

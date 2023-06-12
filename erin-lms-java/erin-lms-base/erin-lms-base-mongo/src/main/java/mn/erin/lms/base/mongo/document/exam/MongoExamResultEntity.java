package mn.erin.lms.base.mongo.document.exam;

/**
 * @author Galsan Bayart.
 */
public class MongoExamResultEntity
{
  String questionId;
  String answer;

  public MongoExamResultEntity(String questionId, String answer)
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

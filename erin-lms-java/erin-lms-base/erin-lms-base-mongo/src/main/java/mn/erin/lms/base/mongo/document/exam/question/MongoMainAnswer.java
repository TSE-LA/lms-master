package mn.erin.lms.base.mongo.document.exam.question;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Galsan Bayart.
 */
public class MongoMainAnswer
{
  List<MongoMainAnswerEntity> answers = new ArrayList<>();

  public MongoMainAnswer(List<MongoMainAnswerEntity> answers)
  {
    this.answers = answers;
  }

  public List<MongoMainAnswerEntity> getAnswers()
  {
    return answers;
  }

  public void setAnswers(List<MongoMainAnswerEntity> answers)
  {
    this.answers = answers;
  }

}

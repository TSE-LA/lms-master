package mn.erin.lms.base.rest.model.exam;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Galsan Bayart.
 */
public class RestMainAnswer
{
  List<RestMainAnswerEntity> answers = new ArrayList<>();

  public RestMainAnswer(List<RestMainAnswerEntity> answers)
  {
    this.answers = answers;
  }

  public RestMainAnswer()
  {
  }

  public List<RestMainAnswerEntity> getAnswers()
  {
    return answers;
  }

  public void setAnswers(List<RestMainAnswerEntity> answers)
  {
    this.answers = answers;
  }
}

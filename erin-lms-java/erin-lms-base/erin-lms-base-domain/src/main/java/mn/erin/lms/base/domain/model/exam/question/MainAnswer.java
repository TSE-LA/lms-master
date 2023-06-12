package mn.erin.lms.base.domain.model.exam.question;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Galsan Bayart.
 */
public class MainAnswer extends QuestionAnswerAbstract
{
  List<MainAnswerEntity> answers = new ArrayList<>();


  public MainAnswer(List<MainAnswerEntity> answers)
  {
    this.answers = answers;
  }

  public MainAnswer()
  {
  }

  public List<MainAnswerEntity> getAnswers()
  {
    return answers;
  }

  public void setAnswers(List<MainAnswerEntity> answers)
  {
    this.answers = answers;
  }
}

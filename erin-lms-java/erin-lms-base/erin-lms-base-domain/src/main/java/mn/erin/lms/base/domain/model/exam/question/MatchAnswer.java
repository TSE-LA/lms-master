package mn.erin.lms.base.domain.model.exam.question;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Galsan Bayart.
 */
public class MatchAnswer extends QuestionAnswerAbstract
{
  List<MatchAnswerEntity> firstLine = new ArrayList<>();
  List<MatchAnswerEntity> secondLine = new ArrayList<>();

  public MatchAnswer(List<MatchAnswerEntity> firstLine, List<MatchAnswerEntity> secondLine)
  {
    this.firstLine = firstLine;
    this.secondLine = secondLine;
  }

  public List<MatchAnswerEntity> getFirstLine()
  {
    return firstLine;
  }

  public void setFirstLine(List<MatchAnswerEntity> firstLine)
  {
    this.firstLine = firstLine;
  }

  public List<MatchAnswerEntity> getSecondLine()
  {
    return secondLine;
  }

  public void setSecondLine(List<MatchAnswerEntity> secondLine)
  {
    this.secondLine = secondLine;
  }
}

package mn.erin.lms.base.domain.usecase.exam.dto.question;

import java.util.List;

/**
 * @author Galsan Bayart.
 */
public class MatchQuestionDto implements QuestionDtoAbstract
{
  String title;
  List<String> firstLine;
  List<String> secondLine;

  public MatchQuestionDto(String title, List<String> firstLine, List<String> secondLine)
  {
    this.title = title;
    this.firstLine = firstLine;
    this.secondLine = secondLine;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public List<String> getFirstLine()
  {
    return firstLine;
  }

  public void setFirstLine(List<String> firstLine)
  {
    this.firstLine = firstLine;
  }

  public List<String> getSecondLine()
  {
    return secondLine;
  }

  public void setSecondLine(List<String> secondLine)
  {
    this.secondLine = secondLine;
  }
}

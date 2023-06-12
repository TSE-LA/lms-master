package mn.erin.lms.base.domain.usecase.exam.dto.question;

import java.util.Set;

/**
 * @author Galsan Bayart.
 */
public class MainQuestionDto implements QuestionDtoAbstract
{
  String name;
  Set<String> answers;

  public MainQuestionDto(String name, Set<String> answers)
  {
    this.name = name;
    this.answers = answers;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public Set<String> getAnswers()
  {
    return answers;
  }

  public void setAnswers(Set<String> answers)
  {
    this.answers = answers;
  }
}

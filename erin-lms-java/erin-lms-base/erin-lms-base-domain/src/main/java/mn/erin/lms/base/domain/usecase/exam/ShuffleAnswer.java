package mn.erin.lms.base.domain.usecase.exam;

import java.util.Collections;
import java.util.List;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.exam.question.Question;
import mn.erin.lms.base.domain.model.exam.question.QuestionType;

/**
 * @author Galsan Bayart.
 */
public class ShuffleAnswer implements UseCase<List<Question>, Void>
{
  @Override
  public Void execute(List<Question> input) throws UseCaseException
  {
    for (Question question : input)
    {
      if (question.getType() != QuestionType.MATCH)
      {
       Collections.shuffle((question.getAnswers()));
      }
    }
    return null;
  }
}

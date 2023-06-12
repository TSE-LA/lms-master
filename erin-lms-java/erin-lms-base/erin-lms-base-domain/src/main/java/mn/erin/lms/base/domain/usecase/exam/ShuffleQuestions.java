package mn.erin.lms.base.domain.usecase.exam;

import java.util.Collections;
import java.util.List;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.exam.question.Question;

/**
 * @author Galsan Bayart.
 */
public class ShuffleQuestions implements UseCase<List<Question>, Void>
{
  @Override
  public Void execute(List<Question> input) throws UseCaseException
  {
    Collections.shuffle(input);
    return null;
  }
}

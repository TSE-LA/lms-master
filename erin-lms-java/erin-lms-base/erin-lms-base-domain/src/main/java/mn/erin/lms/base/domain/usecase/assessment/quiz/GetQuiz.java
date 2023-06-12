package mn.erin.lms.base.domain.usecase.assessment.quiz;

import java.util.Objects;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.assessment.Quiz;
import mn.erin.lms.base.domain.model.assessment.QuizId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.QuizRepository;
import mn.erin.lms.base.domain.usecase.assessment.dto.QuizDto;

import static mn.erin.lms.base.domain.util.QuizUtils.toQuizDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetQuiz implements UseCase<String, QuizDto>
{
  private final QuizRepository quizRepository;

  public GetQuiz(QuizRepository quizRepository)
  {
    this.quizRepository = Objects.requireNonNull(quizRepository);
  }

  @Override
  public QuizDto execute(String input) throws UseCaseException
  {
    QuizId quizId = QuizId.valueOf(input);

    try
    {
      Quiz quiz = quizRepository.fetchById(quizId);
      return toQuizDto(quiz);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

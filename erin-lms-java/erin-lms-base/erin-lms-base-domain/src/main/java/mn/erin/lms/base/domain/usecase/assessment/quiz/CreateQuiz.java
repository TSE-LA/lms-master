package mn.erin.lms.base.domain.usecase.assessment.quiz;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.assessment.Quiz;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.QuizRepository;
import mn.erin.lms.base.domain.usecase.assessment.dto.CreateQuizInput;
import mn.erin.lms.base.domain.usecase.assessment.dto.QuizDto;

import static mn.erin.lms.base.domain.util.QuizUtils.createQuestionList;
import static mn.erin.lms.base.domain.util.QuizUtils.toQuizDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreateQuiz implements UseCase<CreateQuizInput, QuizDto>
{
  private final QuizRepository quizRepository;

  public CreateQuiz(QuizRepository quizRepository)
  {
    this.quizRepository = Objects.requireNonNull(quizRepository);
  }

  @Override
  public QuizDto execute(CreateQuizInput input) throws UseCaseException
  {
    Validate.notNull(input);

    Quiz quiz;
    try
    {
      if (input.getMaxAttempts() != null && input.getThresholdScore() != null)
      {
        quiz = quizRepository.create(createQuestionList(input.getQuestions()), input.getName(), input.isGraded(), input.getTimeLimit(),
            input.getDueDate(), input.getMaxAttempts(), input.getThresholdScore());
      }
      else
      {
        quiz = quizRepository.create(createQuestionList(input.getQuestions()), input.getName(), input.isGraded(), input.getTimeLimit(),
            input.getDueDate());
      }
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }

    return toQuizDto(quiz);
  }
}

package mn.erin.lms.base.domain.usecase.assessment.quiz;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.assessment.Quiz;
import mn.erin.lms.base.domain.model.assessment.QuizId;
import mn.erin.lms.base.domain.repository.AssessmentRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.repository.QuizRepository;
import mn.erin.lms.base.domain.usecase.assessment.dto.QuizDto;
import mn.erin.lms.base.domain.usecase.assessment.dto.UpdateQuizInput;

import static mn.erin.lms.base.domain.util.QuizUtils.createQuestionList;
import static mn.erin.lms.base.domain.util.QuizUtils.toQuizDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class UpdateQuiz implements UseCase<UpdateQuizInput, QuizDto>
{
  private final QuizRepository quizRepository;
  private final AssessmentRepository assessmentRepository;

  public UpdateQuiz(LmsRepositoryRegistry lmsRepositoryRegistry)
  {
    this.quizRepository = Objects.requireNonNull(lmsRepositoryRegistry.getQuizRepository());
    this.assessmentRepository = Objects.requireNonNull(lmsRepositoryRegistry.getAssessmentRepository());
  }

  @Override
  public QuizDto execute(UpdateQuizInput input) throws UseCaseException
  {
    Validate.notNull(input);

    Quiz quiz;
    try
    {
      QuizId quizId = QuizId.valueOf(input.getQuizId());
      if (input.getMaxAttempts() != null && input.getThresholdScore() != null)
      {
        quiz = quizRepository.update(quizId, createQuestionList(input.getQuestionInfos()), input.getMaxAttempts(), input.getThresholdScore());
        if(input.getAssessmentId() != null){
          assessmentRepository.updateModifiedDate(input.getAssessmentId());
        }
      }
      else
      {
        quiz = quizRepository.update(quizId, createQuestionList(input.getQuestionInfos()));
        if(input.getAssessmentId() != null) {
          assessmentRepository.updateModifiedDate(input.getAssessmentId());
        }
      }
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }

    return toQuizDto(quiz);
  }
}

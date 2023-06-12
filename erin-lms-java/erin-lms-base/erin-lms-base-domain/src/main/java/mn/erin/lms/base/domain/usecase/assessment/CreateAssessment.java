package mn.erin.lms.base.domain.usecase.assessment;

import java.util.Optional;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.AssessmentConstants;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.assessment.Assessment;
import mn.erin.lms.base.domain.model.assessment.Quiz;
import mn.erin.lms.base.aim.user.AuthorId;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.repository.AssessmentRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.repository.QuizRepository;
import mn.erin.lms.base.aim.AuthorIdProvider;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.LmsUseCase;
import mn.erin.lms.base.domain.usecase.assessment.dto.CreateAssessmentInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Instructor.class })
public class CreateAssessment extends LmsUseCase<CreateAssessmentInput, String>
{
  private final AssessmentRepository assessmentRepository;
  private final QuizRepository quizRepository;
  private final AuthorIdProvider authorIdProvider;

  public CreateAssessment(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.assessmentRepository = lmsRepositoryRegistry.getAssessmentRepository();
    this.quizRepository = lmsRepositoryRegistry.getQuizRepository();
    this.authorIdProvider = lmsServiceRegistry.getAuthorIdProvider();
  }

  @Override
  protected String executeImpl(CreateAssessmentInput input) throws UseCaseException
  {
    Validate.notNull(input);
    AuthorId authorId = AuthorId.valueOf(authorIdProvider.getAuthorId());

    Optional<String> description = Optional.ofNullable(input.getDescription());

    try
    {
      Quiz quiz = quizRepository.create(AssessmentConstants.DEFAULT_QUIZ_NAME);

      Assessment assessment;
      if (description.isPresent())
      {
        assessment = assessmentRepository.createAssessment(quiz.getQuizId(), input.getName(), authorId, description.get());
      }
      else
      {
        assessment = assessmentRepository.createAssessment(quiz.getQuizId(), input.getName(), authorId);
      }

      return assessment.getAssessmentId().getId();
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

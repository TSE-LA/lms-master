package mn.erin.lms.base.domain.usecase.exam.question;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.exam.question.QuestionState;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;

/**
 * @author Galsan Bayart
 */
@Authorized(users = { Author.class, Instructor.class, Manager.class, Supervisor.class })
public class CreateQuestionState extends QuestionUseCase<QuestionState, String>
{
  public CreateQuestionState(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  protected String executeImpl(QuestionState input) throws UseCaseException
  {
    return questionStateRepository.create(input).getQuestionStateId().getId();
  }
}

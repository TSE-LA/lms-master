package mn.erin.lms.base.domain.usecase.exam.question;

import java.util.List;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.exam.question.QuestionGroup;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.LmsUseCase;

/**
 * @author Temuulen Naranbold
 */
@Authorized(users = { Author.class, Instructor.class, Manager.class, Supervisor.class })
public class GetQuestionGroupByParentId extends LmsUseCase<String, List<QuestionGroup>>
{
  public GetQuestionGroupByParentId(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  protected List<QuestionGroup> executeImpl(String input) throws UseCaseException
  {
    return lmsRepositoryRegistry.getQuestionGroupRepository().getAll(input, lmsServiceRegistry.getOrganizationIdProvider().getOrganizationId());
  }
}

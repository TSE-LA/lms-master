package mn.erin.lms.base.domain.usecase.exam.question;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.LmsUseCase;
import mn.erin.lms.base.domain.usecase.exam.dto.question.QuestionGroupInput;

/**
 * @author Galsan Bayart
 */
@Authorized(users = { Author.class, Instructor.class, Manager.class, Supervisor.class })
public class CreateQuestionGroup extends LmsUseCase<QuestionGroupInput, String>
{
  public CreateQuestionGroup(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
     super(Objects.requireNonNull(lmsRepositoryRegistry), Objects.requireNonNull(lmsServiceRegistry));
  }

  @Override
  protected String executeImpl(QuestionGroupInput input) throws UseCaseException
  {
    try
    {
      Validate.notNull(input);

      OrganizationId organizationId = OrganizationId.valueOf(lmsServiceRegistry.getOrganizationIdProvider().getOrganizationId());

      return lmsRepositoryRegistry.getQuestionGroupRepository().create(input.getParentId(), input.getName(), organizationId, input.getDescription()).getId().getId();
    }
    catch (NullPointerException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

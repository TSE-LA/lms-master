package mn.erin.lms.base.domain.usecase.exam;

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
import mn.erin.lms.base.domain.usecase.exam.dto.ExamGroupInput;

/**
 * @author Galsan Bayart.
 */
@Authorized(users = { Author.class, Manager.class, Instructor.class, Supervisor.class })
public class CreateExamGroup extends LmsUseCase<ExamGroupInput, String>
{
  public CreateExamGroup(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(Objects.requireNonNull(lmsRepositoryRegistry), Objects.requireNonNull(lmsServiceRegistry));
  }

  @Override
  protected String executeImpl(ExamGroupInput input) throws UseCaseException
  {
    try
    {
      Validate.notNull(input);

      return lmsRepositoryRegistry.getExamGroupRepository().create(
          input.getParentId(),
          input.getName(),
          OrganizationId.valueOf(lmsServiceRegistry.getOrganizationIdProvider().getOrganizationId()),
          input.getDescription()
      ).getId().getId();
    }
    catch (NullPointerException | IllegalArgumentException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

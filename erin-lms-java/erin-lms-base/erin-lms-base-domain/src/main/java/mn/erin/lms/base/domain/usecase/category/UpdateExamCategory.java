package mn.erin.lms.base.domain.usecase.category;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.category.ExamCategoryId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.LmsUseCase;
import mn.erin.lms.base.domain.usecase.category.dto.ExamCategoryInput;

/**
 * @author Temuulen Naranbold
 */
@Authorized(users = { Author.class, Instructor.class, Manager.class, Supervisor.class })
public class UpdateExamCategory extends LmsUseCase<ExamCategoryInput, Void>
{
  public UpdateExamCategory(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(Objects.requireNonNull(lmsRepositoryRegistry), Objects.requireNonNull(lmsServiceRegistry));
  }

  @Override
  protected Void executeImpl(ExamCategoryInput input) throws UseCaseException
  {
    try
    {
      Validate.notNull(input);

      ExamCategoryId id = ExamCategoryId.valueOf(input.getId());

      if (!lmsRepositoryRegistry.getExamCategoryRepository().exists(id))
      {
        throw new UseCaseException("Category not found!");
      }

      OrganizationId organizationId = OrganizationId.valueOf(lmsServiceRegistry.getOrganizationIdProvider().getOrganizationId());

      lmsRepositoryRegistry.getExamCategoryRepository().update(id, organizationId, input.getIndex(), input.getCategoryName(), input.getDescription());
    }
    catch (LmsRepositoryException | NullPointerException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }

    return null;
  }
}

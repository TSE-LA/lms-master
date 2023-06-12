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
import mn.erin.lms.base.domain.repository.ExamCategoryRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.OrganizationIdProvider;
import mn.erin.lms.base.domain.usecase.LmsUseCase;
import mn.erin.lms.base.domain.usecase.category.dto.ExamCategoryInput;

/**
 * @author Temuulen Naranbold
 */
@Authorized(users = { Author.class, Instructor.class, Manager.class, Supervisor.class })
public class CreateExamCategory extends LmsUseCase<ExamCategoryInput, String>
{
  private final ExamCategoryRepository examCategoryRepository;
  private final OrganizationIdProvider organizationIdProvider;

  public CreateExamCategory(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(Objects.requireNonNull(lmsRepositoryRegistry), Objects.requireNonNull(lmsServiceRegistry));
    this.examCategoryRepository = lmsRepositoryRegistry.getExamCategoryRepository();
    this.organizationIdProvider = lmsServiceRegistry.getOrganizationIdProvider();
  }

  @Override
  protected String executeImpl(ExamCategoryInput input) throws UseCaseException
  {
    Validate.notNull(input);
    Validate.notBlank(input.getCategoryName());
    String organizationId = organizationIdProvider.getOrganizationId();

    try
    {
      return examCategoryRepository.create(
          ExamCategoryId.valueOf(input.getParentCategoryId()),
          OrganizationId.valueOf(organizationId),
          input.getIndex(),
          input.getCategoryName(),
          input.getDescription()
      ).getId().getId();
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

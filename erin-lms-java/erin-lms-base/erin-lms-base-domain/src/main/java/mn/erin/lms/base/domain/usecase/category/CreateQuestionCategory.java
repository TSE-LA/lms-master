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
import mn.erin.lms.base.domain.model.category.QuestionCategory;
import mn.erin.lms.base.domain.model.category.QuestionCategoryId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.repository.QuestionCategoryRepository;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.OrganizationIdProvider;
import mn.erin.lms.base.domain.usecase.LmsUseCase;
import mn.erin.lms.base.domain.usecase.category.dto.QuestionCategoryInput;

/**
 * @author Galsan Bayart
 */
@Authorized(users = { Author.class, Instructor.class, Manager.class, Supervisor.class })
public class CreateQuestionCategory extends LmsUseCase<QuestionCategoryInput, String>
{
  private final QuestionCategoryRepository questionCategoryRepository;
  private final OrganizationIdProvider organizationIdProvider;

  public CreateQuestionCategory(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(Objects.requireNonNull(lmsRepositoryRegistry), Objects.requireNonNull(lmsServiceRegistry));
    this.questionCategoryRepository = lmsRepositoryRegistry.getQuestionCategoryRepository();
    this.organizationIdProvider = lmsServiceRegistry.getOrganizationIdProvider();
  }

  @Override
  protected String executeImpl(QuestionCategoryInput input) throws UseCaseException
  {
    String organizationId = organizationIdProvider.getOrganizationId();
    try
    {
      Validate.notNull(input);
      Validate.notBlank(input.getParentCategoryId());
      Validate.notBlank(input.getName());

      QuestionCategory questionCategory = questionCategoryRepository.create(
          QuestionCategoryId.valueOf(input.getParentCategoryId()),
          OrganizationId.valueOf(organizationId),
          input.getIndex(),
          input.getName(),
          input.getDescription()
      );
      return questionCategory.getId().getId();
    }
    catch (LmsRepositoryException | NullPointerException | IllegalArgumentException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

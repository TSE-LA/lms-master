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
import mn.erin.lms.base.domain.model.category.QuestionCategoryId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.repository.QuestionCategoryRepository;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.OrganizationIdProvider;
import mn.erin.lms.base.domain.usecase.LmsUseCase;
import mn.erin.lms.base.domain.usecase.category.dto.QuestionCategoryInput;

/**
 * @author Temuulen Naranbold
 */
@Authorized(users = { Author.class, Instructor.class, Manager.class, Supervisor.class })
public class UpdateQuestionCategory extends LmsUseCase<QuestionCategoryInput, Void>
{
  private final QuestionCategoryRepository questionCategoryRepository;
  private final OrganizationIdProvider organizationIdProvider;

  public UpdateQuestionCategory(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(Objects.requireNonNull(lmsRepositoryRegistry), Objects.requireNonNull(lmsServiceRegistry));
    this.questionCategoryRepository = lmsRepositoryRegistry.getQuestionCategoryRepository();
    this.organizationIdProvider = lmsServiceRegistry.getOrganizationIdProvider();
  }

  @Override
  protected Void executeImpl(QuestionCategoryInput input) throws UseCaseException
  {
    Validate.notNull(input);
    OrganizationId organizationId = OrganizationId.valueOf(organizationIdProvider.getOrganizationId());
    QuestionCategoryId questionCategoryId = QuestionCategoryId.valueOf(input.getQuestionCategoryId());

    if (!questionCategoryRepository.exists(questionCategoryId))
    {
      throw new UseCaseException("Question category not found");
    }

    try
    {
      questionCategoryRepository.update(organizationId, questionCategoryId, input.getIndex(), input.getName(), input.getDescription());
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
    return null;
  }
}

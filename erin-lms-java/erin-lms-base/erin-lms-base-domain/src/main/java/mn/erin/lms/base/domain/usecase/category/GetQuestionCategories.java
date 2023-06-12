package mn.erin.lms.base.domain.usecase.category;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.category.QuestionCategory;
import mn.erin.lms.base.domain.model.category.QuestionCategoryId;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.repository.QuestionCategoryRepository;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.LmsUseCase;
import mn.erin.lms.base.domain.usecase.category.dto.QuestionCategoryDto;

/**
 * @author Temuulen Naranbold
 */
@Authorized(users = { Author.class, Instructor.class, Manager.class, Supervisor.class })
public class GetQuestionCategories extends LmsUseCase<String, List<QuestionCategoryDto>>
{
  private final QuestionCategoryRepository questionCategoryRepository;

  public GetQuestionCategories(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(Objects.requireNonNull(lmsRepositoryRegistry), Objects.requireNonNull(lmsServiceRegistry));
    this.questionCategoryRepository = lmsRepositoryRegistry.getQuestionCategoryRepository();
  }

  @Override
  protected List<QuestionCategoryDto> executeImpl(String input)
  {
    OrganizationId organizationId = OrganizationId.valueOf(lmsServiceRegistry.getOrganizationIdProvider().getOrganizationId());
    Collection<QuestionCategory> questionCategories;

    if (StringUtils.isBlank(input))
    {
      questionCategories = questionCategoryRepository.getAllByOrganizationId(organizationId);
    }
    else
    {
      questionCategories = questionCategoryRepository.listAll(QuestionCategoryId.valueOf(input), organizationId);
    }
    return toOutput(questionCategories);
  }

  private List<QuestionCategoryDto> toOutput(Collection<QuestionCategory> questionCategories)
  {
    List<QuestionCategoryDto> result = new ArrayList<>();

    for (QuestionCategory questionCategory : questionCategories)
    {
      QuestionCategoryDto dto = new QuestionCategoryDto(
          questionCategory.getId().getId(),
          questionCategory.getParentCategoryId() != null ? questionCategory.getParentCategoryId().getId() : null,
          questionCategory.getIndex(),
          questionCategory.getName(),
          questionCategory.getDescription()
      );
      result.add(dto);
    }

    return result;
  }
}

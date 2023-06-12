package mn.erin.lms.base.domain.usecase.category;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.model.category.ExamCategory;
import mn.erin.lms.base.domain.model.category.ExamCategoryId;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.category.dto.ExamCategoryDto;

/**
 * @author Temuulen Naranbold
 */
public class GetExamCategories implements UseCase<String, List<ExamCategoryDto>>
{
  private final LmsRepositoryRegistry lmsRepositoryRegistry;
  private final LmsServiceRegistry lmsServiceRegistry;

  public GetExamCategories(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    this.lmsRepositoryRegistry = Objects.requireNonNull(lmsRepositoryRegistry);
    this.lmsServiceRegistry = Objects.requireNonNull(lmsServiceRegistry);
  }

  @Override
  public List<ExamCategoryDto> execute(String input)
  {
    OrganizationId organizationId = OrganizationId.valueOf(lmsServiceRegistry.getOrganizationIdProvider().getOrganizationId());
    Collection<ExamCategory> examCategories;

    if (StringUtils.isBlank(input))
    {
      examCategories = lmsRepositoryRegistry.getExamCategoryRepository().listAllByOrganizationId(organizationId);
    }
    else
    {
      examCategories = lmsRepositoryRegistry.getExamCategoryRepository().listAll(ExamCategoryId.valueOf(input), organizationId);
    }
    return toOutput(examCategories);
  }

  private List<ExamCategoryDto> toOutput(Collection<ExamCategory> examCategories)
  {
    List<ExamCategoryDto> result = new ArrayList<>();

    for (ExamCategory examCategory : examCategories)
    {
      ExamCategoryDto dto = new ExamCategoryDto(
          examCategory.getId().getId(),
          examCategory.getParentCategoryId() != null ? examCategory.getParentCategoryId().getId() : null,
          examCategory.getIndex(),
          examCategory.getName(),
          examCategory.getDescription()
      );

      result.add(dto);
    }

    return result;
  }
}

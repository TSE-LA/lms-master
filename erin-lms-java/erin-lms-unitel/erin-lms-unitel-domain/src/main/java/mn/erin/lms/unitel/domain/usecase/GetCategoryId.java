package mn.erin.lms.unitel.domain.usecase;

import java.util.List;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.service.OrganizationIdProvider;
import mn.erin.lms.base.domain.usecase.category.GetCourseCategories;
import mn.erin.lms.base.domain.usecase.category.dto.CourseCategoryDto;
import mn.erin.lms.base.domain.usecase.category.dto.GetCourseCategoriesInput;
import mn.erin.lms.base.domain.usecase.enrollment.dto.GetCategoryIdInput;

/**
 *
 * @author Munkh
 */
public class GetCategoryId implements UseCase<GetCategoryIdInput, String>
{
  private final CourseCategoryRepository courseCategoryRepository;
  private final OrganizationIdProvider organizationIdProvider;

  public GetCategoryId(CourseCategoryRepository courseCategoryRepository, OrganizationIdProvider organizationIdProvider)
  {
    this.courseCategoryRepository = courseCategoryRepository;
    this.organizationIdProvider = organizationIdProvider;
  }

  @Override
  public String execute(GetCategoryIdInput input) throws UseCaseException
  {
    GetCourseCategories getCourseCategories = new GetCourseCategories(courseCategoryRepository);
    List<CourseCategoryDto> categories = getCourseCategories.execute(new GetCourseCategoriesInput(organizationIdProvider.getOrganizationId(), input.getParentId()));
    for (CourseCategoryDto categoryDto: categories)
    {
      if (categoryDto.getCategoryName().equalsIgnoreCase(input.getCategoryName()))
      {
        return categoryDto.getCategoryId();
      }
    }
    throw new UseCaseException("Category not found.");
  }
}

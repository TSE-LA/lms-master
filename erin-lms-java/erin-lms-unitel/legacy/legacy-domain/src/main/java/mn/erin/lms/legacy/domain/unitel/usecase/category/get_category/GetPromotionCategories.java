package mn.erin.lms.legacy.domain.unitel.usecase.category.get_category;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import mn.erin.lms.legacy.domain.lms.model.course.CourseCategory;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.unitel.usecase.category.CategoryUseCase;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetPromotionCategories extends CategoryUseCase<Void, Set<GetPromotionCategoryOutput>>
{
  public GetPromotionCategories(CourseCategoryRepository courseCategoryRepository)
  {
    super(courseCategoryRepository);
  }

  @Override
  public Set<GetPromotionCategoryOutput> execute(Void input)
  {
    Collection<CourseCategory> courseCategories = courseCategoryRepository.listAll(COMPANY_ID, CATEGORY_PROMOTION);

    Set<GetPromotionCategoryOutput> result = new HashSet<>();
    for (CourseCategory courseCategory : courseCategories)
    {
      GetPromotionCategoryOutput output = new GetPromotionCategoryOutput(courseCategory.getCategoryId().getId(),
          courseCategory.getParentCategoryId().getId(), courseCategory.getName());
      output.setDescription(courseCategory.getDescription());

      result.add(output);
    }

    return result;
  }
}

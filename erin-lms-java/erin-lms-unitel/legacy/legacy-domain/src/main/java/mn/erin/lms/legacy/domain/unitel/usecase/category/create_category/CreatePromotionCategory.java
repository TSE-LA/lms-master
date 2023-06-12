package mn.erin.lms.legacy.domain.unitel.usecase.category.create_category;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategory;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.unitel.usecase.category.CategoryUseCase;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreatePromotionCategory extends CategoryUseCase<CreatePromotionCategoryInput, CreatePromotionCategoryOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CreatePromotionCategory.class);

  public CreatePromotionCategory(CourseCategoryRepository courseCategoryRepository)
  {
    super(courseCategoryRepository);
  }

  @Override
  public CreatePromotionCategoryOutput execute(CreatePromotionCategoryInput input) throws UseCaseException
  {
    Validate.notNull(input, "Input is required to create a promotion category!");

    try
    {
      CourseCategory courseCategory = courseCategoryRepository.create(COMPANY_ID, CATEGORY_PROMOTION,
          input.getCategoryName(), input.getDescription());

      return new CreatePromotionCategoryOutput(courseCategory.getCategoryId().getId());
    }
    catch (LMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

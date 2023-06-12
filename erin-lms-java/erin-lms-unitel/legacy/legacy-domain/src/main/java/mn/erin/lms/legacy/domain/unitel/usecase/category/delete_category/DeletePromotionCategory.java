package mn.erin.lms.legacy.domain.unitel.usecase.category.delete_category;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.unitel.usecase.category.CategoryUseCase;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class DeletePromotionCategory extends CategoryUseCase<DeletePromotionCategoryInput, Void>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DeletePromotionCategory.class);

  public DeletePromotionCategory(CourseCategoryRepository courseCategoryRepository)
  {
    super(courseCategoryRepository);
  }

  @Override
  public Void execute(DeletePromotionCategoryInput input) throws UseCaseException
  {
    Validate.notNull(input, "Input is required to delete a promotion category!");

    try
    {
      courseCategoryRepository.delete(COMPANY_ID, CATEGORY_PROMOTION, input.getCategoryName());
      return null;
    }
    catch (LMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

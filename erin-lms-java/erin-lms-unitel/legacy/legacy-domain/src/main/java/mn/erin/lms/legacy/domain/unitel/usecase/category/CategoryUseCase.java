package mn.erin.lms.legacy.domain.unitel.usecase.category;

import java.util.Objects;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.legacy.domain.lms.model.course.CompanyId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategoryId;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.unitel.CategoryConstants;

/**
 * @author Bat-Erdene Tsogoo.
 */
public abstract class CategoryUseCase<I, O> implements UseCase<I, O>
{
  protected static final CompanyId COMPANY_ID = new CompanyId(CategoryConstants.COMPANY_ID);
  protected static final CourseCategoryId CATEGORY_PROMOTION = new CourseCategoryId(CategoryConstants.CATEGORY_PROMOTION);

  protected final CourseCategoryRepository courseCategoryRepository;

  public CategoryUseCase(CourseCategoryRepository courseCategoryRepository)
  {
    this.courseCategoryRepository = Objects.requireNonNull(courseCategoryRepository, "CourseCategoryRepository cannot be null!");
  }
}

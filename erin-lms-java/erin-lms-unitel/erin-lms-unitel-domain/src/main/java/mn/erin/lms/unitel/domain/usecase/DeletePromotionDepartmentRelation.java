package mn.erin.lms.unitel.domain.usecase;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class DeletePromotionDepartmentRelation extends CourseUseCase<String, Boolean>
{
  public DeletePromotionDepartmentRelation(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  public Boolean execute(String input) throws UseCaseException
  {
    throw new UnsupportedOperationException();
  }
}

package mn.erin.lms.unitel.domain.usecase;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class ExpirePromotion implements UseCase<Void, Void>
{
  private final CourseRepository courseRepository;
  private final CourseCategoryRepository courseCategoryRepository;

  public ExpirePromotion(LmsRepositoryRegistry lmsRepositoryRegistry)
  {
    this.courseRepository = lmsRepositoryRegistry.getCourseRepository();
    this.courseCategoryRepository = lmsRepositoryRegistry.getCourseCategoryRepository();
  }

  // TODO implement me
  @Override
  public Void execute(Void input)
  {
    return null;
  }
}

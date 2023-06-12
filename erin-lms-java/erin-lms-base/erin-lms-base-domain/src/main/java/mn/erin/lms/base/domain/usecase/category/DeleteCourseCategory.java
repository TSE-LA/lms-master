package mn.erin.lms.base.domain.usecase.category;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;

/**
 * @author Munkh
 */
@Authorized(users = { Author.class })
public class DeleteCourseCategory implements UseCase<String, Boolean>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteCourseCategory.class);
  private final CourseCategoryRepository courseCategoryRepository;
  private final CourseRepository courseRepository;

  public DeleteCourseCategory(CourseCategoryRepository courseCategoryRepository, CourseRepository courseRepository)  {
    this.courseCategoryRepository = courseCategoryRepository;
    this.courseRepository = courseRepository;
  }

  @Override
  public Boolean execute(String input) throws UseCaseException
  {
    Validate.notEmpty(input);
    CourseCategoryId deletingCategoryId = CourseCategoryId.valueOf(input);

    if (!courseCategoryRepository.exists(deletingCategoryId))
    {
      return false;
    }

    List<Course> coursesUnderTheCategory = courseRepository.listAll(deletingCategoryId);

    if (coursesUnderTheCategory != null && !coursesUnderTheCategory.isEmpty())
    {
      throw new UseCaseException("Please delete all the courses under the category ID of: " + input);
    }

    try
    {
      courseCategoryRepository.delete(deletingCategoryId);
    }
    catch (LmsRepositoryException e)
    {
      LOGGER.error(e.getMessage());
      return false;
    }

    return true;
  }
}

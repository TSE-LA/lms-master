package mn.erin.lms.base.domain.usecase.category;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.usecase.category.dto.UpdateAutoEnrollmentInput;

/**
 * @author Munkh
 */
@Authorized(users = { Author.class, Instructor.class })
public class UpdateAutoEnrollment implements UseCase<UpdateAutoEnrollmentInput, Void>
{
  private final CourseCategoryRepository courseCategoryRepository;

  public UpdateAutoEnrollment(CourseCategoryRepository courseCategoryRepository)
  {
    this.courseCategoryRepository = Objects.requireNonNull(courseCategoryRepository);
  }

  @Override
  public Void execute(UpdateAutoEnrollmentInput input) throws UseCaseException
  {
    Validate.notNull(input);
    Validate.notNull(input.getAutoEnroll());

    try
    {
      if (input.getId() != null)
      {
        courseCategoryRepository.updateAutoEnrollment(CourseCategoryId.valueOf(input.getId()), input.getAutoEnroll());
      }
      else
      {
        throw new UseCaseException("Invalid input!");
      }
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }

    return null;
  }
}

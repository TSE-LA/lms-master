package mn.erin.lms.base.domain.usecase;

import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.usecase.category.dto.UpdateAutoEnrollmentsInput;

/**
 * @author Munkh
 */
@Authorized(users = { Author.class, Instructor.class })
public class UpdateAutoEnrollments implements UseCase<UpdateAutoEnrollmentsInput, Void>
{
  private final CourseCategoryRepository courseCategoryRepository;

  public UpdateAutoEnrollments(CourseCategoryRepository courseCategoryRepository)
  {
    this.courseCategoryRepository = Objects.requireNonNull(courseCategoryRepository);
  }

  @Override
  public Void execute(UpdateAutoEnrollmentsInput input) throws UseCaseException
  {
    Validate.notNull(input);
    Validate.notNull(input.getAutoEnroll());

    if (input.getIds() != null && !input.getIds().isEmpty())
    {
      courseCategoryRepository.updateAutoEnrollments(input.getIds().stream().map(CourseCategoryId::valueOf).collect(Collectors.toSet()), input.getAutoEnroll());
    }
    else
    {
      throw new UseCaseException("Invalid input!");
    }

    return null;
  }
}

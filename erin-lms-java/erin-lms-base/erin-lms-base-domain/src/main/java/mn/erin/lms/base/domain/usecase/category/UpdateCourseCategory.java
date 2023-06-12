package mn.erin.lms.base.domain.usecase.category;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.service.OrganizationIdProvider;
import mn.erin.lms.base.domain.usecase.category.dto.UpdateCourseCategoryInput;

/**
 * @author Munkh
 */
@Authorized(users = { Author.class, Instructor.class })
public class UpdateCourseCategory implements UseCase<UpdateCourseCategoryInput, Void>
{
  private final CourseCategoryRepository courseCategoryRepository;
  private final OrganizationIdProvider organizationIdProvider;

  public UpdateCourseCategory(CourseCategoryRepository courseCategoryRepository, OrganizationIdProvider organizationIdProvider)
  {
    this.courseCategoryRepository = Objects.requireNonNull(courseCategoryRepository);
    this.organizationIdProvider = Objects.requireNonNull(organizationIdProvider);
  }

  @Override
  public Void execute(UpdateCourseCategoryInput input) throws UseCaseException
  {
    Validate.notNull(input);
    Validate.notBlank(input.getId());
    Validate.notBlank(input.getName());

    String organizationId = organizationIdProvider.getOrganizationId();

    if (!courseCategoryRepository.exists(CourseCategoryId.valueOf(input.getId())))
    {
      throw new UseCaseException("Category not found!");
    }

    try
    {
      courseCategoryRepository.update(
          OrganizationId.valueOf(organizationId),
          CourseCategoryId.valueOf(input.getId()),
          input.getName(),
          input.getDescription(),
          input.isAutoEnroll()
      );
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }

    return null;
  }
}

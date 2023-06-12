package mn.erin.lms.base.domain.usecase.category;

import java.util.Objects;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.category.CourseCategory;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.service.OrganizationIdProvider;
import mn.erin.lms.base.domain.usecase.category.dto.CreateCourseCategoryInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Instructor.class })
public class CreateCourseCategory implements UseCase<CreateCourseCategoryInput, String>
{
  private final CourseCategoryRepository courseCategoryRepository;
  private final OrganizationIdProvider organizationIdProvider;

  public CreateCourseCategory(CourseCategoryRepository courseCategoryRepository, OrganizationIdProvider organizationIdProvider)
  {
    this.courseCategoryRepository = Objects.requireNonNull(courseCategoryRepository);
    this.organizationIdProvider = Objects.requireNonNull(organizationIdProvider);
  }

  @Override
  public String execute(CreateCourseCategoryInput input) throws UseCaseException
  {
    String organizationId = organizationIdProvider.getOrganizationId();

    try
    {
      CourseCategory courseCategory = courseCategoryRepository.create(
          OrganizationId.valueOf(organizationId),
          CourseCategoryId.valueOf(input.getParentId()),
          input.getCategoryName(),
          input.getDescription(),
          input.isAutoEnroll()
      );
      return courseCategory.getCourseCategoryId().getId();
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

package mn.erin.lms.base.domain.usecase.category;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.category.CourseCategory;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Learner;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.usecase.category.dto.CourseCategoryDto;
import mn.erin.lms.base.domain.usecase.category.dto.GetCourseCategoriesInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Author.class, Manager.class, Supervisor.class, Instructor.class, Learner.class })
public class GetCourseCategories implements UseCase<GetCourseCategoriesInput, List<CourseCategoryDto>>
{
  private final CourseCategoryRepository courseCategoryRepository;

  public GetCourseCategories(CourseCategoryRepository courseCategoryRepository)
  {
    this.courseCategoryRepository = Objects.requireNonNull(courseCategoryRepository);
  }

  @Override
  public List<CourseCategoryDto> execute(GetCourseCategoriesInput input)
  {
    Validate.notNull(input);

    Collection<CourseCategory> courseCategories;
    if (input.isAutoEnroll() != null)
    {
      if (StringUtils.isBlank(input.getParentCategoryId()))
      {
        courseCategories = courseCategoryRepository.listAllByAutoEnrollment(OrganizationId.valueOf(input.getOrganizationId()));
      }
      else
      {
        courseCategories = courseCategoryRepository.listAllByAutoEnrollment(OrganizationId.valueOf(input.getOrganizationId()),
            CourseCategoryId.valueOf(input.getParentCategoryId()));
      }
    }
    else if (StringUtils.isBlank(input.getParentCategoryId()))
    {
      courseCategories = courseCategoryRepository.listAll(OrganizationId.valueOf(input.getOrganizationId()));
    }
    else
    {
      courseCategories = courseCategoryRepository.listAll(OrganizationId.valueOf(input.getOrganizationId()),
          CourseCategoryId.valueOf(input.getParentCategoryId()));
    }
    return toOutput(courseCategories);
  }

  private List<CourseCategoryDto> toOutput(Collection<CourseCategory> courseCategories)
  {
    List<CourseCategoryDto> result = new ArrayList<>();

    for (CourseCategory courseCategory : courseCategories)
    {
      CourseCategoryDto dto = new CourseCategoryDto(courseCategory.getCourseCategoryId().getId(),
          courseCategory.getParentCategoryId() != null ? courseCategory.getParentCategoryId().getId() : null, courseCategory.getName());
      dto.setDescription(courseCategory.getDescription());
      dto.setAutoEnroll(courseCategory.isAutoEnroll());

      result.add(dto);
    }

    return result;
  }
}

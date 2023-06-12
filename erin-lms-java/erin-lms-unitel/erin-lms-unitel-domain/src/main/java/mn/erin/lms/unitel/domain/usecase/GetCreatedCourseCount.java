package mn.erin.lms.unitel.domain.usecase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.AuthorId;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.category.CourseCategory;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.CourseType;
import mn.erin.lms.base.domain.model.course.PublishStatus;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.unitel.domain.usecase.dto.CourseCountByCategory;
import mn.erin.lms.unitel.domain.usecase.dto.CourseCountDto;
import mn.erin.lms.unitel.domain.usecase.dto.GetCourseCountInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Author.class })
public class GetCreatedCourseCount extends CourseUseCase<GetCourseCountInput, List<CourseCountDto>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetCreatedCourseCount.class);


  public GetCreatedCourseCount(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  public List<CourseCountDto> execute(GetCourseCountInput input) throws UseCaseException
  {
    String organizationId = lmsServiceRegistry.getOrganizationIdProvider().getOrganizationId();
    Collection<CourseCategory> courseCategories = courseCategoryRepository.listAll(OrganizationId.valueOf(organizationId),
        CourseCategoryId.valueOf(input.getParentCategoryId()));
    return toOutput(courseCategories, input);
  }

  private List<CourseCountDto> toOutput(Collection<CourseCategory> courseCategories, GetCourseCountInput input)
  {
    List<CourseType> courseTypes = getCourseTypes(input.getCourseTypes());
    LmsDepartmentService departmentService = lmsServiceRegistry.getDepartmentService();
    String currentUserDepartment = departmentService.getCurrentDepartmentId();
    Set<DepartmentId> belongingDepartments = departmentService.getSubDepartments(currentUserDepartment)
        .stream().map(DepartmentId::valueOf).collect(Collectors.toSet());

    List<CourseCountDto> result = new ArrayList<>();

    for (CourseType courseType : courseTypes)
    {
      Set<CourseCountByCategory> courseCountByCategories = new HashSet<>();
      for (CourseCategory courseCategory : courseCategories)
      {
        Integer publishedCount;
        Integer unpublishedCount;
        if (!StringUtils.isBlank(input.getAuthorId()))
        {
          publishedCount = courseRepository.countCreatedCourses(belongingDepartments, courseCategory.getCourseCategoryId(), PublishStatus.PUBLISHED,
              courseType, input.getStartDate(), input.getEndDate(), AuthorId.valueOf(input.getAuthorId()));
          unpublishedCount = courseRepository.countCreatedCourses(belongingDepartments, courseCategory.getCourseCategoryId(), PublishStatus.UNPUBLISHED,
              courseType, input.getStartDate(), input.getEndDate(), AuthorId.valueOf(input.getAuthorId()));
          Integer pendingPromotionCount = courseRepository.countCreatedCourses(belongingDepartments, courseCategory.getCourseCategoryId(),
              PublishStatus.PENDING, courseType, input.getStartDate(), input.getEndDate(), AuthorId.valueOf(input.getAuthorId()));
          unpublishedCount += pendingPromotionCount;
        }
        else
        {
          publishedCount = courseRepository.countCreatedCourses(belongingDepartments, courseCategory.getCourseCategoryId(), PublishStatus.PUBLISHED,
              courseType, input.getStartDate(), input.getEndDate());
          unpublishedCount = courseRepository.countCreatedCourses(belongingDepartments, courseCategory.getCourseCategoryId(), PublishStatus.UNPUBLISHED,
              courseType, input.getStartDate(), input.getEndDate());

          Integer pendingPromotionCount = courseRepository.countCreatedCourses(belongingDepartments, courseCategory.getCourseCategoryId(),
              PublishStatus.PENDING, courseType, input.getStartDate(), input.getEndDate());
          unpublishedCount += pendingPromotionCount;
        }
        courseCountByCategories.add(new CourseCountByCategory(courseCategory.getName(), unpublishedCount, publishedCount, courseCategory.getCourseCategoryId().getId()));
      }
      result.add(new CourseCountDto(courseType.getType(), courseCountByCategories));
    }

    return result;
  }

  private List<CourseType> getCourseTypes(Set<String> courseTypes)
  {
    List<CourseType> result = new ArrayList<>();
    for (String courseType : courseTypes)
    {
      try
      {
        result.add(getCourseType(courseType));
      }
      catch (UseCaseException e)
      {
        LOGGER.error(e.getMessage(), e);
      }
    }

    return result;
  }
}

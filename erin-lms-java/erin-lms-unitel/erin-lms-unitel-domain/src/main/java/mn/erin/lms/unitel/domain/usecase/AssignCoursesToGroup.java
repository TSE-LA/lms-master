package mn.erin.lms.unitel.domain.usecase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDepartmentRelation;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.PublishStatus;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.UseCaseDelegator;
import mn.erin.lms.base.domain.usecase.category.GetCourseCategories;
import mn.erin.lms.base.domain.usecase.category.dto.CourseCategoryDto;
import mn.erin.lms.base.domain.usecase.category.dto.GetCourseCategoriesInput;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.base.domain.usecase.course.GetCourses;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.base.domain.usecase.course.dto.GetCoursesInput;
import mn.erin.lms.base.domain.usecase.enrollment.dto.AssignCoursesToGroupInput;
import mn.erin.lms.base.domain.usecase.enrollment.dto.GetCategoryIdInput;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Munkh
 */
public class AssignCoursesToGroup extends CourseUseCase<AssignCoursesToGroupInput, Void>
{
  private final GroupRepository groupRepository;
  public AssignCoursesToGroup(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry, GroupRepository groupRepository)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.groupRepository = groupRepository;
  }

  @Override
  public Void execute(AssignCoursesToGroupInput input) throws UseCaseException
  {
    GetCourses getCourses = new GetCourses(lmsRepositoryRegistry, lmsServiceRegistry,
        (UseCaseDelegator<GetCoursesInput, List<CourseDto>>) lmsServiceRegistry.getUseCaseResolver().getUseCaseDelegator(GetCourses.class.getName()));

    List<CourseDto> courses = new ArrayList<>();

    if(StringUtils.isBlank(input.getCategoryName()))
    {
      GetCourseCategories getCourseCategories = new GetCourseCategories(lmsRepositoryRegistry.getCourseCategoryRepository());
      List<CourseCategoryDto> categories = getCourseCategories
          .execute(new GetCourseCategoriesInput(lmsServiceRegistry.getOrganizationIdProvider().getOrganizationId(),
              "online-course"));
      for(CourseCategoryDto category: categories)
      {
        GetCoursesInput getCoursesInput = new GetCoursesInput(category.getCategoryId());
        getCoursesInput.setPublishStatus(PublishStatus.PUBLISHED.name());

        courses.addAll(getCourses.execute(getCoursesInput));
      }
    }
    else
    {
      GetCategoryId getCategoryId = new GetCategoryId(lmsRepositoryRegistry.getCourseCategoryRepository(), lmsServiceRegistry.getOrganizationIdProvider());
      String categoryId = getCategoryId.execute(new GetCategoryIdInput("online-course", input.getCategoryName()));
      GetCoursesInput getCoursesInput = new GetCoursesInput(categoryId);
      getCoursesInput.setPublishStatus(PublishStatus.PUBLISHED.name());

      courses.addAll(getCourses.execute(getCoursesInput));
    }

    Set<String> parentGroups = groupRepository.getParentGroupIds(input.getParentId());
    courses.removeIf(course -> !course.getAssignedDepartments().contains(input.getParentId()) &&
        course.getAssignedDepartments().stream().noneMatch(parentGroups::contains));

    for(CourseDto courseDto: courses)
    {
      Course course = getCourse(CourseId.valueOf(courseDto.getId()));
      CourseDepartmentRelation courseDepartmentRelation = course.getCourseDepartmentRelation();
      Set<DepartmentId> assignedDepartments = courseDepartmentRelation.getAssignedDepartments();
      Set<DepartmentId> addedDepartments = new HashSet<>(assignedDepartments);
      addedDepartments.add(DepartmentId.valueOf(input.getId()));
      courseDepartmentRelation.setAssignedDepartments(addedDepartments);

      try
      {
        courseRepository.update(course.getCourseId(), courseDepartmentRelation);
      }
      catch (LmsRepositoryException e)
      {
        throw new UseCaseException(e.getMessage(), e);
      }
    }
    return null;
  }
}

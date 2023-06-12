package mn.erin.lms.base.domain.usecase.category;

import org.junit.Before;
import org.junit.Test;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.model.category.CourseCategory;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.service.OrganizationIdProvider;
import mn.erin.lms.base.domain.usecase.category.dto.CreateCourseCategoryInput;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Dashnyam Bayarsaikhan
 */
public class CreateCourseCategoryTest
{
  private CourseCategoryRepository courseCategoryRepository;
  private OrganizationIdProvider organizationIdProvider;

  private CreateCourseCategory createCourseCategory;

  @Before
  public void setUp()
  {
    courseCategoryRepository = mock(CourseCategoryRepository.class);
    organizationIdProvider = mock(OrganizationIdProvider.class);
    createCourseCategory = new CreateCourseCategory(courseCategoryRepository, organizationIdProvider);
  }

  @Test(expected = NullPointerException.class)
  public void inputIsNull() throws UseCaseException
  {
    createCourseCategory.execute(null);
  }

  @Test(expected = NullPointerException.class)
  public void create_course_category_input_is_null() throws UseCaseException
  {
    createCourseCategory.execute(new CreateCourseCategoryInput(null, null));
  }

  @Test
  public void create_category_success() throws UseCaseException, LmsRepositoryException
  {
    CreateCourseCategoryInput input = new CreateCourseCategoryInput("parentId", "categoryId");

    CourseCategory courseCategory = new CourseCategory(OrganizationId.valueOf("organizationId"),
        CourseCategoryId.valueOf("courseCategoryId"),
        CourseCategoryId.valueOf("parentId"), "name", false);
    when(organizationIdProvider.getOrganizationId()).thenReturn("organizationId");
    when(courseCategoryRepository.create(any(OrganizationId.class), any(CourseCategoryId.class), any(), any(), anyBoolean())).thenReturn(courseCategory);
    String categoryId = createCourseCategory.execute(input);
    assertEquals("courseCategoryId", categoryId);
  }

  @Test(expected = NullPointerException.class)
  public void organizationId_null_throws_null_pointer_exception_test() throws UseCaseException
  {
    CreateCourseCategoryInput input = new CreateCourseCategoryInput("parentId", "categoryId");
    when(organizationIdProvider.getOrganizationId()).thenReturn(null);
    createCourseCategory.execute(input);
  }

  @Test(expected = NullPointerException.class)
  public void create_course_returns_null_throws_null_pointer_exception() throws LmsRepositoryException, UseCaseException
  {
    CreateCourseCategoryInput input = new CreateCourseCategoryInput("parentId", "categoryId");
    when(organizationIdProvider.getOrganizationId()).thenReturn("organizationId");
    when(courseCategoryRepository.create(any(OrganizationId.class), any(CourseCategoryId.class), any(), any(), anyBoolean())).thenReturn(null);
    createCourseCategory.execute(input);
  }
}
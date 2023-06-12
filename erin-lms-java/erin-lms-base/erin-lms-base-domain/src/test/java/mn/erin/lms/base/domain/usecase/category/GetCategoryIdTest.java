package mn.erin.lms.base.domain.usecase.category;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.model.category.CourseCategory;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.service.OrganizationIdProvider;
import mn.erin.lms.base.domain.usecase.enrollment.dto.GetCategoryIdInput;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Dashnyam Bayarsaikhan
 */
public class GetCategoryIdTest
{

  private CourseCategoryRepository courseCategoryRepository;

  private OrganizationIdProvider organizationIdProvider;

  private GetCategoryId getCategoryId;

  @Before
  public void setUp()
  {
    courseCategoryRepository = mock(CourseCategoryRepository.class);
    organizationIdProvider = mock(OrganizationIdProvider.class);
    getCategoryId = new GetCategoryId(courseCategoryRepository, organizationIdProvider);
  }

  @Test(expected = NullPointerException.class)
  public void null_input_throws_null_pointer_exception_test() throws UseCaseException
  {
    getCategoryId.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void category_not_found_throws_usecase_exception_test() throws UseCaseException
  {
    GetCategoryIdInput getCategoryIdInput = new GetCategoryIdInput("parentId", "categoryName");
    when(organizationIdProvider.getOrganizationId()).thenReturn("organizationId");
    when(courseCategoryRepository.listAll(any(OrganizationId.class), any(CourseCategoryId.class))).thenReturn(new ArrayList<>());
    getCategoryId.execute(getCategoryIdInput);
  }

  @Test
  public void assert_when_category_is_found() throws UseCaseException
  {
    Collection<CourseCategory> courseCategories = new ArrayList<>();

    for (int i = 0; i < 5; i++)
    {
      CourseCategory courseCategory = new CourseCategory(OrganizationId.valueOf("organizationId"),
          CourseCategoryId.valueOf("courseCategoryId" + i), CourseCategoryId.valueOf("parentCategoryId"),
          "categoryName", false);

      courseCategories.add(courseCategory);

    }

    CourseCategory categoryToFind = new CourseCategory(OrganizationId.valueOf("organizationId"),
        CourseCategoryId.valueOf("courseCategoryId"), CourseCategoryId.valueOf("parentCategoryId"),
        "uniqueCategoryName", false);

    courseCategories.add(categoryToFind);

    GetCategoryIdInput getCategoryIdInput = new GetCategoryIdInput("parentId", "uniqueCategoryName");
    when(courseCategoryRepository.listAll(any(OrganizationId.class), any(CourseCategoryId.class))).thenReturn(courseCategories);
    when(organizationIdProvider.getOrganizationId()).thenReturn("organizationId");
    String categoryId = getCategoryId.execute(getCategoryIdInput);
    assertEquals(categoryToFind.getCourseCategoryId().getId(), categoryId);
  }

  @Test(expected = NullPointerException.class)
  public void organizationId_null_throws_null_pointer_test() throws UseCaseException
  {
    GetCategoryIdInput getCategoryIdInput = new GetCategoryIdInput("parentId", "uniqueCategoryName");
    when(organizationIdProvider.getOrganizationId()).thenReturn(null);
    getCategoryId.execute(getCategoryIdInput);
  }
}
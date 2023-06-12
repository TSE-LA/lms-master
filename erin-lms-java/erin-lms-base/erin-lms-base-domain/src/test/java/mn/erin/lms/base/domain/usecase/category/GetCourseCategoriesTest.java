package mn.erin.lms.base.domain.usecase.category;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.model.category.CourseCategory;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.usecase.category.dto.CourseCategoryDto;
import mn.erin.lms.base.domain.usecase.category.dto.GetCourseCategoriesInput;
import mn.erin.lms.base.domain.usecase.enrollment.dto.GetCategoryIdInput;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Dashnyam Bayarsaikhan
 */
public class GetCourseCategoriesTest
{
  private final static int TOTAL = 5;
  private CourseCategoryRepository courseCategoryRepository;

  private GetCourseCategories getCourseCategories;
  private List<CourseCategory> courseCategories;

  @Before
  public void setUp()
  {
    courseCategoryRepository = mock(CourseCategoryRepository.class);
    getCourseCategories = new GetCourseCategories(courseCategoryRepository);

    courseCategories = new ArrayList<>();

    for (int i = 0; i < TOTAL; i++)
    {
      CourseCategory courseCategory = new CourseCategory(OrganizationId.valueOf("organizationId"),
          CourseCategoryId.valueOf("courseCategoryId" + i), CourseCategoryId.valueOf("parentCategoryId"),
          "categoryName", false);

      courseCategories.add(courseCategory);
    }
  }

  @Test(expected = NullPointerException.class)
  public void input_is_null_throws_null_pointer_exception_test()
  {
    getCourseCategories.execute(null);
  }

  @Test
  public void autoEnroll_is_not_null_parent_categoryId_is_blank_success_test()
  {
    GetCourseCategoriesInput getCourseCategoriesInput = new GetCourseCategoriesInput("organizationId", "");
    when(courseCategoryRepository.listAllByAutoEnrollment(any(OrganizationId.class))).thenReturn(courseCategories);
    getCourseCategoriesInput.setAutoEnroll(true);
    List<CourseCategoryDto> categoryDtos = getCourseCategories.execute(getCourseCategoriesInput);
    assertEquals(TOTAL, categoryDtos.size());
  }

  @Test
  public void autoEnroll_is_not_null_parent_categoryId_is_not_blank_success_test()
  {
    GetCourseCategoriesInput getCourseCategoriesInput = new GetCourseCategoriesInput("organizationId", "parent");
    when(courseCategoryRepository.listAllByAutoEnrollment(any(OrganizationId.class), any(CourseCategoryId.class)))
        .thenReturn(courseCategories);
    getCourseCategoriesInput.setAutoEnroll(true);
    List<CourseCategoryDto> categoryDtos = getCourseCategories.execute(getCourseCategoriesInput);
    assertEquals(TOTAL, categoryDtos.size());
  }

  @Test
  public void autoEnroll_is_null_parent_categoryId_is_blank_success_test()
  {
    GetCourseCategoriesInput getCourseCategoriesInput = new GetCourseCategoriesInput("organizationId", "");
    when(courseCategoryRepository.listAll(any(OrganizationId.class))).thenReturn(courseCategories);
    getCourseCategoriesInput.setAutoEnroll(null);
    List<CourseCategoryDto> categoryDtos = getCourseCategories.execute(getCourseCategoriesInput);
    assertEquals(TOTAL, categoryDtos.size());
  }

  @Test
  public void autoEnroll_is_null_parent_categoryId_is_not_blank_success_test()
  {
    GetCourseCategoriesInput getCourseCategoriesInput = new GetCourseCategoriesInput("organizationId", "parentCategory");
    when(courseCategoryRepository.listAll(any(OrganizationId.class), any(CourseCategoryId.class))).thenReturn(courseCategories);
    getCourseCategoriesInput.setAutoEnroll(null);
    List<CourseCategoryDto> categoryDtos = getCourseCategories.execute(getCourseCategoriesInput);
    assertEquals(TOTAL, categoryDtos.size());
  }

  @Test
  public void autoEnroll_is_false_parent_categoryId_is_not_blank_success_test()
  {
    GetCourseCategoriesInput getCourseCategoriesInput = new GetCourseCategoriesInput("organizationId", "parentCategory");
    when(courseCategoryRepository.listAllByAutoEnrollment(any(OrganizationId.class), any(CourseCategoryId.class))).thenReturn(courseCategories);
    getCourseCategoriesInput.setAutoEnroll(false);
    List<CourseCategoryDto> categoryDtos = getCourseCategories.execute(getCourseCategoriesInput);
    assertEquals(TOTAL, categoryDtos.size());
  }

  @Test(expected = NullPointerException.class)
  public void course_category_repository_returns_null_throws_null_pointer_exception()
  {
    GetCourseCategoriesInput getCourseCategoriesInput = new GetCourseCategoriesInput("organizationId", "parentCategory");
    when(courseCategoryRepository.listAll(any(OrganizationId.class), any(CourseCategoryId.class)))
        .thenReturn(null);
    getCourseCategories.execute(getCourseCategoriesInput);
  }
}